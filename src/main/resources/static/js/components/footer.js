/**
 * Static Footer Component
 * Renders consistent branding, company navigation, and legal links across all pages.
 */

function renderFooter() {
    const footer = document.getElementById("footer");
    if (!footer) return;

    footer.innerHTML = `
    <footer class="footer">
      <div class="footer-container">
        <!-- Branding Section -->
        <div class="footer-branding">
          <h3>CarePortal</h3>
          <p>© Copyright ${new Date().getFullYear()} CarePortal. All rights reserved.</p>
        </div>

        <!-- Link Columns -->
        <div class="footer-links">
          <!-- Column 1: Company -->
          <div class="footer-column">
            <h4>Company</h4>
            <a href="#">About</a>
            <a href="#">Careers</a>
            <a href="#">Press</a>
          </div>

          <!-- Column 2: Support -->
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Account</a>
            <a href="#">Help Center</a>
            <a href="#">Contact</a>
          </div>

          <!-- Column 3: Legals -->
          <div class="footer-column">
            <h4>Legals</h4>
            <a href="#">Terms</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Licensing</a>
          </div>
        </div>
      </div>
    </footer>
  `;
}

// Execute on DOM load
document.addEventListener("DOMContentLoaded", renderFooter);

// Execute immediately if DOM is already ready
renderFooter();