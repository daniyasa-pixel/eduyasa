/* Wait for the real MathJax engine and serialize dynamic typesetting. */
(function (root) {
  'use strict';
  function createRenderer({ getMathJax = () => root.MathJax, script, timeoutMs = 20000, onError = () => {} } = {}) {
    let loading;
    let queue = Promise.resolve();

    function waitForEngine() {
      const current = getMathJax();
      if (typeof current?.typesetPromise === 'function') {
        return Promise.resolve(current.startup?.promise).then(() => current);
      }
      if (!loading) {
        loading = new Promise((resolve, reject) => {
          let timer;
          const cleanup = () => {
            clearTimeout(timer);
            script?.removeEventListener('load', loaded);
            script?.removeEventListener('error', failed);
          };
          const failed = () => { cleanup(); reject(new Error('MathJax could not be loaded.')); };
          const loaded = () => {
            cleanup();
            const engine = getMathJax();
            Promise.resolve(engine?.startup?.promise).then(() => {
              if (typeof engine?.typesetPromise !== 'function') throw new Error('MathJax is not ready.');
              resolve(engine);
            }).catch(reject);
          };
          if (!script || script.dataset.mathjaxState === 'error') { failed(); return; }
          if (script.dataset.mathjaxState === 'loaded') { loaded(); return; }
          script.addEventListener('load', loaded, { once: true });
          script.addEventListener('error', failed, { once: true });
          timer = setTimeout(failed, timeoutMs);
        });
      }
      return loading;
    }

    function clear(element) {
      if (element) getMathJax()?.typesetClear?.([element]);
    }

    async function render(element) {
      // Non-math lessons can continue even if the CDN is unavailable.
      if (!element || !/\$|\\\(|\\\[/.test(element.textContent || '')) return true;
      const task = queue.then(async () => {
        const engine = await waitForEngine();
        await engine.typesetPromise([element]);
        return true;
      });
      queue = task.catch(() => false);
      try { return await task; }
      catch (error) { onError(error, element); return false; }
    }
    return { render, clear };
  }
  const api = { createRenderer };
  if (typeof module !== 'undefined' && module.exports) module.exports = api;
  else root.EduyasaMath = api;
})(typeof window !== 'undefined' ? window : globalThis);
