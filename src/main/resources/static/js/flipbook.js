(function () {
  const container = document.getElementById("flipbookViewer");
  if (!container) return;

  const pdfUrl = container.dataset.pdfUrl;
  if (!pdfUrl) return;

  const statusEl = document.getElementById("flipbookStatus");
  const wrapEl = document.getElementById("flipbookWrap");
  const sizerEl = document.getElementById("flipbookSizer");
  const navEl = document.getElementById("flipbookNav");
  const prevBtn = document.getElementById("flipPrev");
  const nextBtn = document.getElementById("flipNext");
  const pageIndicator = document.getElementById("flipPageIndicator");

  pdfjsLib.GlobalWorkerOptions.workerSrc = "/vendor/pdfjs/pdf.worker.min.js";

  async function renderPdfToImages(url) {
    const pdf = await pdfjsLib.getDocument(url).promise;
    const scale = 1.5;
    const images = [];
    let aspectRatio = 0.7; // page width / page height, fallback ~ portrait

    for (let i = 1; i <= pdf.numPages; i++) {
      const page = await pdf.getPage(i);
      const viewport = page.getViewport({ scale });
      if (i === 1) aspectRatio = viewport.width / viewport.height;

      const canvas = document.createElement("canvas");
      canvas.width = viewport.width;
      canvas.height = viewport.height;
      await page.render({ canvasContext: canvas.getContext("2d"), viewport })
        .promise;
      images.push(canvas.toDataURL("image/jpeg", 0.85));

      if (statusEl) {
        statusEl.textContent =
          "Loading flipbook… (" + i + " / " + pdf.numPages + ")";
      }
    }

    return { images, aspectRatio };
  }

  // Reserve space below the book for the Prev/Next nav row + breathing room
  const RESERVED_BOTTOM = 90;
  const MIN_BOOK_HEIGHT = 320;

  // Manual size control: 1 = the book fills a full screen's worth of height
  // (viewport height, minus room for the Prev/Next row). Since it sits below
  // the site header, this generally means scrolling down slightly to bring
  // it fully into view — then it fills essentially the whole screen. Lower
  // this (e.g. 0.85) if you'd rather it fit alongside the header with no
  // scrolling at all; raise it (e.g. 1.1) for an even bigger book.
  const SIZE_SCALE = 1;

  function computeAvailableHeight() {
    return Math.max(
      MIN_BOOK_HEIGHT,
      (window.innerHeight - RESERVED_BOTTOM) * SIZE_SCALE
    );
  }

  renderPdfToImages(pdfUrl)
    .then(({ images, aspectRatio }) => {
      if (!images.length) throw new Error("PDF has no pages");

      if (statusEl) statusEl.style.display = "none";
      wrapEl.style.display = "flex";

      const availableHeight = computeAvailableHeight();
      const availableWidth = wrapEl.clientWidth;

      // Two-page spread aspect ratio (width:height), derived from the PDF's
      // own page proportions rather than an assumed one.
      const spreadAspect = aspectRatio * 2;
      let targetHeight = availableHeight;
      let targetWidth = targetHeight * spreadAspect;
      if (targetWidth > availableWidth) {
        targetWidth = availableWidth;
        targetHeight = targetWidth / spreadAspect;
      }
      targetHeight = Math.round(targetHeight);
      targetWidth = Math.round(targetWidth);

      // "stretch" mode always forces the element it's constructed on to
      // width:100% (overwriting any inline width we set on it directly), and
      // derives its internal aspect-ratio wrapper's height from THAT width.
      // So the fixed pixel size has to live one level up, on a plain sizer
      // div, letting #flipbookViewer's 100% resolve against that instead —
      // otherwise the library's wrapper ends up taller than the visible book
      // and swallows clicks on the nav buttons beneath it.
      sizerEl.style.width = targetWidth + "px";
      sizerEl.style.height = targetHeight + "px";

      const pageFlip = new St.PageFlip(container, {
        width: Math.round(targetWidth / 2),
        height: targetHeight,
        size: "stretch",
        minWidth: 220,
        maxWidth: targetWidth,
        minHeight: MIN_BOOK_HEIGHT,
        maxHeight: targetHeight,
        showCover: true,
        maxShadowOpacity: 0.5,
      });
      pageFlip.loadFromImages(images);

      const updateIndicator = () => {
        pageIndicator.textContent =
          pageFlip.getCurrentPageIndex() + 1 + " / " + images.length;
      };
      updateIndicator();
      pageFlip.on("flip", updateIndicator);

      navEl.style.display = "flex";
      prevBtn.addEventListener("click", () => pageFlip.flipPrev());
      nextBtn.addEventListener("click", () => pageFlip.flipNext());
    })
    .catch((err) => {
      console.error("Flipbook failed to load", err);
      if (statusEl) statusEl.textContent = "Could not load the flipbook viewer.";
    });
})();
