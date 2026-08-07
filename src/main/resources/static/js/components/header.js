/**
 * Dynamic Header Component
 * Renders role-specific navigation and manages authentication state across pages.
 */

document.addEventListener("DOMContentLoaded", () => {
    renderHeader();
});

function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;

    // 1. Clear session if visiting root homepage
    if (window.location.pathname.endsWith("/") || window.location.pathname.endsWith("/index.html")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // 2. Validate session token for protected roles
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // 3. Construct header markup based on role
    let headerContent = `<div class="header-wrapper"><div class="logo"><a href="/">CarePortal</a></div><nav class="nav-menu">`;

    if (role === "admin") {
        headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutBtn" class="nav-link">Logout</a>`;
    } else if (role === "doctor") {
        headerContent += `
      <a href="/templates/doctor/doctorDashboard.html" id="homeBtn" class="nav-link">Home</a>
      <a href="#" id="logoutBtn" class="nav-link">Logout</a>`;
    } else if (role === "loggedPatient") {
        headerContent += `
      <a href="/pages/patientDashboard.html" id="homeBtn" class="nav-link">Home</a>
      <a href="#" id="appointmentsBtn" class="nav-link">Appointments</a>
      <a href="#" id="logoutBtn" class="nav-link">Logout</a>`;
    } else {
        // Default / "patient" role
        headerContent += `
      <button id="loginBtn" class="button">Login</button>
      <button id="signupBtn" class="button">Sign Up</button>`;
    }

    headerContent += `</nav></div>`;

    // 4. Inject HTML into container
    headerDiv.innerHTML = headerContent;

    // 5. Attach event handlers to dynamic DOM elements
    attachHeaderButtonListeners(role);
}

function attachHeaderButtonListeners(role) {
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            if (typeof window.openModal === "function") {
                window.openModal("addDoctor");
            }
        });
    }

    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            if (role === "loggedPatient") {
                logoutPatient();
            } else {
                logout();
            }
        });
    }

    const loginBtn = document.getElementById("loginBtn");
    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            window.location.href = "/";
        });
    }

    const signupBtn = document.getElementById("signupBtn");
    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            window.location.href = "/";
        });
    }
}

// Global Logout for Admin/Doctor
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

// Logout for Patient (retains unauthenticated patient state)
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}