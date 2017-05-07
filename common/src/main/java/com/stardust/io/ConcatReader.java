package com.stardust.io;


/*
 * Copyright (C) 2004 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See COPYING.TXT for details.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A reader which reads sequentially from multiple sources.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.04.00
 */
public class ConcatReader extends Reader {

    /**
     * Current index to readerQueue
     *
     * @since ostermillerutils 1.04.01
     */
    private int readerQueueIndex = 0;

    /**
     * Queue of readers that have yet to be read from.
     *
     * @since ostermillerutils 1.04.01
     */
    private ArrayList<Reader> readerQueue = new ArrayList<>();

    /**
     * A cache of the current reader from the readerQueue
     * to avoid unneeded access to the queue which must
     * be synchronized.
     *
     * @since ostermillerutils 1.04.01
     */
    private Reader currentReader = null;

    /**
     * true iff the client may add more readers.
     *
     * @since ostermillerutils 1.04.01
     */
    private boolean doneAddingReaders = false;


    /**
     * Causes the addReader method to throw IllegalStateException
     * and read() methods to return -1 (end of stream)
     * when there is no more available data.
     * <p>
     * Calling this method when this class is no longer accepting
     * more readers has no effect.
     *
     * @since ostermillerutils 1.04.01
     */
    public void lastReaderAdded() {
        doneAddingReaders = true;
    }

    /**
     * Add the given reader to the queue of readers from which to
     * concatenate data.
     *
     * @param in Reader to add to the concatenation.
     * @throws IllegalStateException if more readers can't be added because lastReaderAdded() has been called, close() has been called, or a constructor with reader parameters was used.
     * @since ostermillerutils 1.04.01
     */
    public void addReader(Reader in) {
        synchronized (readerQueue) {
            if (in == null) throw new NullPointerException();
            if (closed) throw new IllegalStateException("ConcatReader has been closed");
            if (doneAddingReaders)
                throw new IllegalStateException("Cannot add more readers - the last reader has already been added.");
            readerQueue.add(in);
        }
    }

    /**
     * Add the given reader to the queue of readers from which to
     * concatenate data.
     *
     * @param in Reader to add to the concatenation.
     * @throws IllegalStateException if more readers can't be added because lastReaderAdded() has been called, close() has been called, or a constructor with reader parameters was used.
     * @throws NullPointerException  the array of readers, or any of the contents is null.
     * @since ostermillerutils 1.04.01
     */
    public void addReaders(Reader[] in) {
        for (Reader element : in) {
            addReader(element);
        }
    }

    /**
     * Gets the current reader, looking at the next
     * one in the list if the current one is null.
     *
     * @since ostermillerutils 1.04.01
     */
    private Reader getCurrentReader() {
        if (currentReader == null && readerQueueIndex < readerQueue.size()) {
            synchronized (readerQueue) {
                // reader queue index is advanced only by the nextReader()
                // method.  Don't do it here.
                currentReader = readerQueue.get(readerQueueIndex);
            }
        }
        return currentReader;
    }

    /**
     * Indicate that we are done with the current reader and we should
     * advance to the next reader.
     *
     * @since ostermillerutils 1.04.01
     */
    private void advanceToNextReader() {
        currentReader = null;
        readerQueueIndex++;
    }

    /**
     * True iff this the close() method has been called on this stream.
     *
     * @since ostermillerutils 1.04.00
     */
    private boolean closed = false;

    /**
     * Create a new reader that can dynamically accept new sources.
     * <p>
     * New sources should be added using the addReader() method.
     * When all sources have been added the lastReaderAdded() should
     * be called so that read methods can return -1 (end of stream).
     * <p>
     * Adding new sources may by interleaved with read calls.
     *
     * @since ostermillerutils 1.04.01
     */
    public ConcatReader() {
        // Empty Constructor
    }

    /**
     * Create a new reader with one source.
     * <p>
     * When using this constructor, more readers cannot
     * be added later, and calling addReader() will
     * throw an illegal state Exception.
     *
     * @param in reader to use as a source.
     * @throws NullPointerException if in is null
     * @since ostermillerutils 1.04.00
     */
    public ConcatReader(Reader in) {
        addReader(in);
        lastReaderAdded();
    }

    /**
     * Create a new reader with two sources.
     * <p>
     * When using this constructor, more readers cannot
     * be added later, and calling addReader() will
     * throw an illegal state Exception.
     *
     * @param in1 first reader to use as a source.
     * @param in2 second reader to use as a source.
     * @throws NullPointerException if either source is null.
     * @since ostermillerutils 1.04.00
     */
    public ConcatReader(Reader in1, Reader in2) {
        addReader(in1);
        addReader(in2);
        lastReaderAdded();
    }

    /**
     * Create a new reader with an arbitrary number of sources.
     * <p>
     * When using this constructor, more readers cannot
     * be added later, and calling addReader() will
     * throw an illegal state Exception.
     *
     * @param in readers to use as a sources.
     * @throws NullPointerException if the input array on any element is null.
     * @since ostermillerutils 1.04.00
     */
    public ConcatReader(Reader[] in) {
        addReaders(in);
        lastReaderAdded();
    }

    /**
     * Read a single character. This method will block until a
     * character is available, an I/O error occurs, or the end of all underlying
     * streams are reached.
     * <p>
     * If this class in not done accepting readers and the end of the last known
     * stream is reached, this method will block forever unless another thread
     * adds a reader or interrupts.
     *
     * @return The character read, as an integer in the range 0 to 65535 (0x00-0xffff),
     * or -1 if the end of the stream has been reached
     * @throws IOException - If an I/O error occurs
     * @since ostermillerutils 1.04.00
     */
    @Override
    public int read() throws IOException {
        if (closed) throw new IOException("Reader closed");
        int r = -1;
        while (r == -1) {
            Reader in = getCurrentReader();
            if (in == null) {
                if (doneAddingReaders) return -1;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException iox) {
                    throw new IOException("Interrupted");
                }
            } else {
                r = in.read();
                if (r == -1) advanceToNextReader();
            }
        }
        return r;
    }

    /**
     * Read characters into an array. This method will block until some input is available, an
     * I/O error occurs, or the end of all underlying
     * streams are reached.
     * <p>
     * If this class in not done accepting readers and the end of the last known
     * stream is reached, this method will block forever unless another thread
     * adds a reader or interrupts.
     *
     * @param cbuf - Destination buffer
     * @return The number of characters read, or -1 if the end of the stream has been reached
     * @throws IOException          - If an I/O error occurs
     * @throws NullPointerException - If the buffer is null.
     * @since ostermillerutils 1.04.00
     */
    @Override
    public int read(char[] cbuf) throws IOException {
        return read(cbuf, 0, cbuf.length);
    }

    /**
     * Read characters into a portion of an array. This method will block until
     * some input is available, an I/O error occurs, or the end of all underlying
     * streams are reached.
     * <p>
     * If this class in not done accepting readers and the end of the last known
     * stream is reached, this method will block forever unless another thread
     * adds a reader or interrupts.
     *
     * @param cbuf Destination buffer
     * @param off  Offset at which to start storing characters
     * @param len  Maximum number of characters to read
     * @return The number of characters read, or -1 if the end of the stream has been reached
     * @throws IOException               - If an I/O error occurs
     * @throws NullPointerException      - If the buffer is null.
     * @throws IndexOutOfBoundsException - if length or offset are not possible.
     * @since ostermillerutils 1.04.00
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > cbuf.length) throw new IndexOutOfBoundsException();
        if (closed) throw new IOException("Reader closed");
        int r = -1;
        while (r == -1) {
            Reader in = getCurrentReader();
            if (in == null) {
                if (doneAddingReaders) return -1;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException iox) {
                    throw new IOException("Interrupted");
                }
            } else {
                r = in.read(cbuf, off, len);
                if (r == -1) advanceToNextReader();
            }
        }
        return r;
    }

    /**
     * Skip characters. This method will block until some characters are
     * available, an I/O error occurs, or the end of the stream is reached.
     * <p>
     * If this class in not done accepting readers and the end of the last known
     * stream is reached, this method will block forever unless another thread
     * adds a reader or interrupts.
     *
     * @param n the number of characters to skip
     * @return The number of characters actually skipped
     * @throws IllegalArgumentException If n is negative.
     * @throws IOException              If an I/O error occurs
     * @since ostermillerutils 1.04.00
     */
    @Override
    public long skip(long n) throws IOException {
        if (closed) throw new IOException("Reader closed");
        if (n <= 0) return 0;
        long s = -1;
        while (s <= 0) {
            Reader in = getCurrentReader();
            if (in == null) {
                if (doneAddingReaders) return 0;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException iox) {
                    throw new IOException("Interrupted");
                }
            } else {
                s = in.skip(n);
                // When nothing was skipped it is a bit of a puzzle.
                // The most common cause is that the end of the underlying
                // stream was reached.  In which case calling skip on it
                // will always return zero.  If somebody were calling skip
                // until it skipped everything they needed, there would
                // be an infinite loop if we were to return zero here.
                // If we get zero, let us try to read one character so
                // we can see if we are at the end of the stream.  If so,
                // we will move to the next.
                if (s <= 0) {
                    // read() will advance to the next stream for us, so don't do it again
                    s = ((read() == -1) ? -1 : 1);
                }
            }

        }
        return s;
    }

    /**
     * Tell whether this stream is ready to be read.
     *
     * @return True if the next read() is guaranteed not to block for input,
     * false otherwise. Note that returning false does not guarantee that the next
     * read will block.
     * @throws IOException If an I/O error occurs
     * @since ostermillerutils 1.04.00
     */
    @Override
    public boolean ready() throws IOException {
        if (closed) throw new IOException("Reader closed");
        Reader in = getCurrentReader();
        if (in == null) return false;
        return in.ready();
    }

    /**
     * Close the stream and any underlying streams.
     * Once a stream has been closed, further read(), ready(), mark(), or reset()
     * invocations will throw an IOException. Closing a previously-closed stream,
     * however, has no effect.
     *
     * @throws IOException If an I/O error occurs
     * @since ostermillerutils 1.04.00
     */
    @Override
    public void close() throws IOException {
        if (closed) return;
        for (Reader reader : readerQueue) {
            reader.close();
        }
        closed = true;
    }

    /**
     * Mark not supported.
     *
     * @throws IOException because mark is not supported.
     * @since ostermillerutils 1.04.00
     */
    @Override
    public void mark(int readlimit) throws IOException {
        throw new IOException("Mark not supported");
    }

    /**
     * Reset not supported.
     *
     * @throws IOException because reset is not supported.
     * @since ostermillerutils 1.04.00
     */
    @Override
    public void reset() throws IOException {
        throw new IOException("Reset not supported");
    }

    /**
     * Mark not supported.
     *
     * @return false
     * @since ostermillerutils 1.04.00
     */
    @Override
    public boolean markSupported() {
        return false;
    }
}