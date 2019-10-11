package com.stardust.autojs.engine.module;

import org.mozilla.javascript.commonjs.module.provider.DefaultUrlConnectionExpiryCalculator;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase;
import org.mozilla.javascript.commonjs.module.provider.ParsedContentType;
import org.mozilla.javascript.commonjs.module.provider.UrlConnectionExpiryCalculator;
import org.mozilla.javascript.commonjs.module.provider.UrlConnectionSecurityDomainProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;


/**
 * A URL-based script provider that can load modules against a set of base
 * privileged and fallback URIs. It is deliberately not named "URI provider"
 * but a "URL provider" since it actually only works against those URIs that
 * are URLs (and the JRE has a protocol handler for them). It creates cache
 * validators that are suitable for use with both file: and http: URL
 * protocols. Specifically, it is able to use both last-modified timestamps and
 * ETags for cache revalidation, and follows the HTTP cache expiry calculation
 * model, and allows for fallback heuristic expiry calculation when no server
 * specified expiry is provided.
 *
 * @author Attila Szegedi
 * @version $Id: UrlModuleSourceProvider.java,v 1.4 2011/04/07 20:26:12 hannes%helma.at Exp $
 */
public class UrlModuleSourceProvider extends ModuleSourceProviderBase {
    private static final long serialVersionUID = 1L;

    private final Iterable<URI> privilegedUris;
    private final Iterable<URI> fallbackUris;
    private final UrlConnectionSecurityDomainProvider
            urlConnectionSecurityDomainProvider;
    private final UrlConnectionExpiryCalculator urlConnectionExpiryCalculator;

    /**
     * Creates a new module script provider that loads modules against a set of
     * privileged and fallback URIs. It will use a fixed default cache expiry
     * of 60 seconds, and provide no security domain objects for the resource.
     *
     * @param privilegedUris an iterable providing the privileged URIs. Can be
     *                       null if no privileged URIs are used.
     * @param fallbackUris   an iterable providing the fallback URIs. Can be
     *                       null if no fallback URIs are used.
     */
    public UrlModuleSourceProvider(Iterable<URI> privilegedUris,
                                   Iterable<URI> fallbackUris) {
        this(privilegedUris, fallbackUris,
                new DefaultUrlConnectionExpiryCalculator(), null);
    }

    /**
     * Creates a new module script provider that loads modules against a set of
     * privileged and fallback URIs. It will use the specified heuristic cache
     * expiry calculator and security domain provider.
     *
     * @param privilegedUris                      an iterable providing the privileged URIs. Can be
     *                                            null if no privileged URIs are used.
     * @param fallbackUris                        an iterable providing the fallback URIs. Can be
     *                                            null if no fallback URIs are used.
     * @param urlConnectionExpiryCalculator       the calculator object for heuristic
     *                                            calculation of the resource expiry, used when no expiry is provided by
     *                                            the server of the resource. Can be null, in which case the maximum age
     *                                            of cached entries without validation will be zero.
     * @param urlConnectionSecurityDomainProvider object that provides security
     *                                            domain objects for the loaded sources. Can be null, in which case the
     *                                            loaded sources will have no security domain associated with them.
     */
    public UrlModuleSourceProvider(Iterable<URI> privilegedUris,
                                   Iterable<URI> fallbackUris,
                                   UrlConnectionExpiryCalculator urlConnectionExpiryCalculator,
                                   UrlConnectionSecurityDomainProvider urlConnectionSecurityDomainProvider) {
        this.privilegedUris = privilegedUris;
        this.fallbackUris = fallbackUris;
        this.urlConnectionExpiryCalculator = urlConnectionExpiryCalculator;
        this.urlConnectionSecurityDomainProvider =
                urlConnectionSecurityDomainProvider;
    }

    @Override
    protected ModuleSource loadFromPrivilegedLocations(
            String moduleId, Object validator)
            throws IOException, URISyntaxException {
        return loadFromPathList(moduleId, validator, privilegedUris);
    }

    @Override
    protected ModuleSource loadFromFallbackLocations(
            String moduleId, Object validator)
            throws IOException, URISyntaxException {
        return loadFromPathList(moduleId, validator, fallbackUris);
    }

    private ModuleSource loadFromPathList(String moduleId,
                                          Object validator, Iterable<URI> paths)
            throws IOException, URISyntaxException {
        if (paths == null) {
            return null;
        }
        for (URI path : paths) {
            final ModuleSource moduleSource = loadFromUri(
                    path.resolve(moduleId), path, validator);
            if (moduleSource != null) {
                return moduleSource;
            }
        }
        return null;
    }

    @Override
    protected ModuleSource loadFromUri(URI uri, URI base, Object validator)
            throws IOException, URISyntaxException {
        // We expect modules to have a ".js" file name extension ...
        URI fullUri = new URI(uri + ".js");
        ModuleSource source = loadFromActualUri(fullUri, base, validator);
        // ... but for compatibility we support modules without extension,
        // or ids with explicit extension.
        return source != null ?
                source : loadFromActualUri(uri, base, validator);
    }

    protected ModuleSource loadFromActualUri(URI uri, URI base, Object validator)
            throws IOException {
        final URL url = new URL(base == null ? null : base.toURL(), uri.toString());
        final long request_time = System.currentTimeMillis();
        final URLConnection urlConnection = openUrlConnection(url);
        final URLValidator applicableValidator;
        if (validator instanceof URLValidator) {
            final URLValidator uriValidator = ((URLValidator) validator);
            applicableValidator = uriValidator.appliesTo(uri) ? uriValidator :
                    null;
        } else {
            applicableValidator = null;
        }
        if (applicableValidator != null) {
            applicableValidator.applyConditionals(urlConnection);
        }
        try {
            urlConnection.connect();
            if (applicableValidator != null &&
                    applicableValidator.updateValidator(urlConnection,
                            request_time, urlConnectionExpiryCalculator)) {
                close(urlConnection);
                return NOT_MODIFIED;
            }

            return new ModuleSource(getReader(urlConnection),
                    getSecurityDomain(urlConnection), uri, base,
                    new URLValidator(uri, urlConnection, request_time,
                            urlConnectionExpiryCalculator));
        } catch (FileNotFoundException e) {
            return null;
        } catch (RuntimeException e) {
            close(urlConnection);
            throw e;
        } catch (IOException e) {
            close(urlConnection);
            throw e;
        }
    }

    protected Reader getReader(URLConnection urlConnection)
            throws IOException {
        return new InputStreamReader(urlConnection.getInputStream(),
                getCharacterEncoding(urlConnection));
    }

    protected String getCharacterEncoding(URLConnection urlConnection) {
        final ParsedContentType pct = new ParsedContentType(
                urlConnection.getContentType());
        final String encoding = pct.getEncoding();
        if (encoding != null) {
            return encoding;
        }
        final String contentType = pct.getContentType();
        if (contentType != null && contentType.startsWith("text/")) {
            return "8859_1";
        }
        return "utf-8";
    }

    protected Object getSecurityDomain(URLConnection urlConnection) {
        return urlConnectionSecurityDomainProvider == null ? null :
                urlConnectionSecurityDomainProvider.getSecurityDomain(
                        urlConnection);
    }

    private void close(URLConnection urlConnection) {
        try {
            urlConnection.getInputStream().close();
        } catch (IOException e) {
            onFailedClosingUrlConnection(urlConnection, e);
        }
    }

    /**
     * Override if you want to get notified if the URL connection fails to
     * close. Does nothing by default.
     *
     * @param urlConnection the connection
     * @param cause         the cause it failed to close.
     */
    protected void onFailedClosingUrlConnection(URLConnection urlConnection,
                                                IOException cause) {
    }

    /**
     * Can be overridden in subclasses to customize the URL connection opening
     * process. By default, just calls {@link URL#openConnection()}.
     *
     * @param url the URL
     * @return a connection to the URL.
     * @throws IOException if an I/O error occurs.
     */
    protected URLConnection openUrlConnection(URL url) throws IOException {
        return url.openConnection();
    }

    @Override
    protected boolean entityNeedsRevalidation(Object validator) {
        return !(validator instanceof URLValidator)
                || ((URLValidator) validator).entityNeedsRevalidation();
    }

    private static class URLValidator implements Serializable {
        private static final long serialVersionUID = 1L;

        private final URI uri;
        private final long lastModified;
        private final String entityTags;
        private long expiry;

        public URLValidator(URI uri, URLConnection urlConnection,
                            long request_time, UrlConnectionExpiryCalculator
                                    urlConnectionExpiryCalculator) {
            this.uri = uri;
            this.lastModified = urlConnection.getLastModified();
            this.entityTags = getEntityTags(urlConnection);
            expiry = calculateExpiry(urlConnection, request_time,
                    urlConnectionExpiryCalculator);
        }

        boolean updateValidator(URLConnection urlConnection, long request_time,
                                UrlConnectionExpiryCalculator urlConnectionExpiryCalculator)
                throws IOException {
            boolean isResourceChanged = isResourceChanged(urlConnection);
            if (!isResourceChanged) {
                expiry = calculateExpiry(urlConnection, request_time,
                        urlConnectionExpiryCalculator);
            }
            return isResourceChanged;
        }

        private boolean isResourceChanged(URLConnection urlConnection)
                throws IOException {
            if (urlConnection instanceof HttpURLConnection) {
                return ((HttpURLConnection) urlConnection).getResponseCode() ==
                        HttpURLConnection.HTTP_NOT_MODIFIED;
            }
            return lastModified != urlConnection.getLastModified();
        }

        private long calculateExpiry(URLConnection urlConnection,
                                     long request_time, UrlConnectionExpiryCalculator
                                             urlConnectionExpiryCalculator) {
            if ("no-cache".equals(urlConnection.getHeaderField("Pragma"))) {
                return 0L;
            }
            final String cacheControl = urlConnection.getHeaderField(
                    "Cache-Control");
            if (cacheControl != null) {
                if (cacheControl.indexOf("no-cache") != -1) {
                    return 0L;
                }
                final int max_age = getMaxAge(cacheControl);
                if (-1 != max_age) {
                    final long response_time = System.currentTimeMillis();
                    final long apparent_age = Math.max(0, response_time -
                            urlConnection.getDate());
                    final long corrected_received_age = Math.max(apparent_age,
                            urlConnection.getHeaderFieldInt("Age", 0) * 1000L);
                    final long response_delay = response_time - request_time;
                    final long corrected_initial_age = corrected_received_age +
                            response_delay;
                    final long creation_time = response_time -
                            corrected_initial_age;
                    return max_age * 1000L + creation_time;
                }
            }
            final long explicitExpiry = urlConnection.getHeaderFieldDate(
                    "Expires", -1L);
            if (explicitExpiry != -1L) {
                return explicitExpiry;
            }
            return urlConnectionExpiryCalculator == null ? 0L :
                    urlConnectionExpiryCalculator.calculateExpiry(urlConnection);
        }

        private int getMaxAge(String cacheControl) {
            final int maxAgeIndex = cacheControl.indexOf("max-age");
            if (maxAgeIndex == -1) {
                return -1;
            }
            final int eq = cacheControl.indexOf('=', maxAgeIndex + 7);
            if (eq == -1) {
                return -1;
            }
            final int comma = cacheControl.indexOf(',', eq + 1);
            final String strAge;
            if (comma == -1) {
                strAge = cacheControl.substring(eq + 1);
            } else {
                strAge = cacheControl.substring(eq + 1, comma);
            }
            try {
                return Integer.parseInt(strAge);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        private String getEntityTags(URLConnection urlConnection) {
            final List<String> etags = urlConnection.getHeaderFields().get("ETag");
            if (etags == null || etags.isEmpty()) {
                return null;
            }
            final StringBuilder b = new StringBuilder();
            final Iterator<String> it = etags.iterator();
            b.append(it.next());
            while (it.hasNext()) {
                b.append(", ").append(it.next());
            }
            return b.toString();
        }

        boolean appliesTo(URI uri) {
            return this.uri.equals(uri);
        }

        void applyConditionals(URLConnection urlConnection) {
            if (lastModified != 0L) {
                urlConnection.setIfModifiedSince(lastModified);
            }
            if (entityTags != null && entityTags.length() > 0) {
                urlConnection.addRequestProperty("If-None-Match", entityTags);
            }
        }

        boolean entityNeedsRevalidation() {
            return System.currentTimeMillis() > expiry;
        }
    }
}