
const stringTimesN = (n, char) => Array(n + 1).join(char)

// Adapted from https://gist.github.com/sente/1083506
function prettifyXml(xmlInput, options) {
  options = options || {};
  let newlineOption = options.newline || '\n';
  let indentOption = options.indent || 4;
  let margin = options.margin || '';
  const indentString = stringTimesN(indentOption, ' ')

  let formatted = '';
  const regex = /(>)(<)(\/*)/g;
  const xml = xmlInput.replace(regex, '$1' + newlineOption + '$2$3');
  let pad = 0;
  xml.split(/\r?\n/).forEach(l => {
    const line = l.trim();

    let indent = 0;
    if (line.match(/.+<\/\w[^>]*>$/)) {
      indent = 0;
    } else if (line.match(/^<\/\w/)) {
      // Somehow istanbul doesn't see the else case as covered, although it is. Skip it.
      /* istanbul ignore else  */
      if (pad !== 0) {
        pad -= 1
      }
    } else if (line.match(/^<\w([^>]*[^\/])?>.*$/)) {
      indent = 1
    } else {
      indent = 0
    }

    const padding = stringTimesN(pad, indentString);
    formatted += margin + padding + line + newlineOption // eslint-disable-line prefer-template
    pad += indent
  })

  return formatted.trim()
}

// For non-es2015 usage
module.exports = prettifyXml