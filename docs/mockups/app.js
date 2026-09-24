// Supernova TV Interactive UI Engine & D-Pad Controller Logic

document.addEventListener('DOMContentLoaded', () => {
  // Screen Navigation Handlers
  const navItems = document.querySelectorAll('.nav-item');
  const screens = document.querySelectorAll('.screen-surface');

  window.showScreen = function(targetId) {
    screens.forEach(screen => {
      if (screen.id === targetId) {
        screen.classList.add('active');
      } else {
        screen.classList.remove('active');
      }
    });

    // Update Top Navigation Bar active state
    navItems.forEach(item => {
      if (item.getAttribute('data-target') === targetId) {
        item.classList.add('active');
      } else {
        item.classList.remove('active');
      }
    });

    // Auto-focus first focusable element on screen load
    const targetScreen = document.getElementById(targetId);
    if (targetScreen) {
      const firstFocusable = targetScreen.querySelector('[tabindex="0"], button, input, select');
      if (firstFocusable) {
        firstFocusable.focus();
      }
    }
  };

  // Top Nav Click Events
  navItems.forEach(item => {
    item.addEventListener('click', () => {
      const targetScreenId = item.getAttribute('data-target');
      if (targetScreenId) {
        showScreen(targetScreenId);
      }
    });
  });

  // Home Hero Actions
  const btnHeroPlay = document.getElementById('btn-hero-play');
  const btnHeroDetails = document.getElementById('btn-hero-details');

  if (btnHeroPlay) {
    btnHeroPlay.addEventListener('click', () => showScreen('player-hud-screen'));
  }
  if (btnHeroDetails) {
    btnHeroDetails.addEventListener('click', () => showScreen('details-screen'));
  }

  // Card Action Listeners (Clicking poster or wide card opens Details or Player)
  document.addEventListener('click', (e) => {
    const card = e.target.closest('.card, .table-row');
    if (!card) return;

    const action = card.getAttribute('data-action');
    const title = card.getAttribute('data-title');

    if (action === 'resume') {
      showScreen('player-hud-screen');
    } else if (action === 'details' || title) {
      if (title) updateDetailsScreen(title);
      showScreen('details-screen');
    }
  });

  // Dynamic Details Screen Updater
  function updateDetailsScreen(title) {
    const titleElem = document.getElementById('details-title-text');
    const synopsisElem = document.getElementById('details-synopsis-text');
    if (titleElem) titleElem.textContent = title;
    if (synopsisElem) {
      synopsisElem.textContent = `Detailed metadata, high-bitrate video stream specifications and cast details loaded for ${title}.`;
    }
  }

  // View Switcher (Grid <-> List)
  const btnViewGrid = document.getElementById('btn-view-grid');
  const btnViewList = document.getElementById('btn-view-list');
  const btnBackToGrid = document.getElementById('btn-back-to-grid');

  if (btnViewGrid) btnViewGrid.addEventListener('click', () => showScreen('movies-grid-screen'));
  if (btnViewList) btnViewList.addEventListener('click', () => showScreen('movies-list-screen'));
  if (btnBackToGrid) btnBackToGrid.addEventListener('click', () => showScreen('movies-grid-screen'));

  // Movie Details Actions
  const btnDetailsBack = document.getElementById('btn-details-back');
  const btnDetailsPlay = document.getElementById('btn-details-play');
  const btnDetailsResume = document.getElementById('btn-details-resume');

  if (btnDetailsBack) btnDetailsBack.addEventListener('click', () => showScreen('movies-grid-screen'));
  if (btnDetailsPlay) btnDetailsPlay.addEventListener('click', () => showScreen('player-hud-screen'));
  if (btnDetailsResume) btnDetailsResume.addEventListener('click', () => showScreen('player-hud-screen'));

  // Player Exit
  const btnPlayerExit = document.getElementById('btn-player-exit');
  if (btnPlayerExit) btnPlayerExit.addEventListener('click', () => showScreen('details-screen'));

  // Player Play/Pause Toggle
  const btnPlayerPlayPause = document.getElementById('btn-player-playpause');
  if (btnPlayerPlayPause) {
    btnPlayerPlayPause.addEventListener('click', () => {
      const icon = btnPlayerPlayPause.querySelector('svg');
      btnPlayerPlayPause.classList.toggle('playing');
    });
  }

  // Settings Rail Category Navigation
  const railItems = document.querySelectorAll('.rail-item');
  const categories = document.querySelectorAll('.settings-category');

  railItems.forEach(item => {
    item.addEventListener('click', () => {
      const catId = item.getAttribute('data-cat');
      railItems.forEach(r => r.classList.remove('active'));
      categories.forEach(c => c.classList.remove('active'));

      item.classList.add('active');
      const targetCat = document.getElementById(catId);
      if (targetCat) targetCat.classList.add('active');
    });
  });

  // Simulated TV Remote Control D-Pad Navigation (Arrow keys + Enter + Backspace/Escape)
  document.addEventListener('keydown', (e) => {
    const focusable = Array.from(document.querySelectorAll('.screen-surface.active [tabindex="0"], .top-nav [tabindex="0"], .quick-nav-helper [tabindex="0"]'))
      .filter(el => el.offsetWidth > 0 && el.offsetHeight > 0);

    const activeEl = document.activeElement;
    let currentIndex = focusable.indexOf(activeEl);

    switch (e.key) {
      case 'ArrowRight':
        if (currentIndex < focusable.length - 1) {
          focusable[currentIndex + 1].focus();
          e.preventDefault();
        }
        break;
      case 'ArrowLeft':
        if (currentIndex > 0) {
          focusable[currentIndex - 1].focus();
          e.preventDefault();
        }
        break;
      case 'ArrowDown':
        if (currentIndex + 4 < focusable.length) {
          focusable[currentIndex + 4].focus();
          e.preventDefault();
        }
        break;
      case 'ArrowUp':
        if (currentIndex - 4 >= 0) {
          focusable[currentIndex - 4].focus();
          e.preventDefault();
        }
        break;
      case 'Escape':
      case 'Backspace':
        // Go back to Home or previous screen
        const activeScreen = document.querySelector('.screen-surface.active');
        if (activeScreen && activeScreen.id !== 'home-screen') {
          showScreen('home-screen');
          e.preventDefault();
        }
        break;
    }
  });

  // Update Clock every second
  function updateClock() {
    const clockElem = document.getElementById('top-clock');
    if (clockElem) {
      const now = new Date();
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      clockElem.textContent = `${hours}:${minutes}`;
    }
  }
  setInterval(updateClock, 1000);
  updateClock();
});
