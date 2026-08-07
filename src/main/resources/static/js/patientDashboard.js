import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";

document.addEventListener("DOMContentLoaded", () => {
    // 1. Initial Load of Doctor Cards
    loadDoctorCards();

    // 2. Bind Modal Trigger Event Listeners for Login and Signup
    const signupBtn = document.getElementById("patientSignup");
    if (signupBtn) {
        signupBtn.addEventListener("click", () => openModal("patientSignup"));
    }

    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) {
        loginBtn.addEventListener("click", () => openModal("patientLogin"));
    }

    // 3. Attach Event Listeners for Search and Filter Controls
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }
    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }
    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }
});

/**
 * Fetches all doctors from the service and renders them into the page.
 */
export async function loadDoctorCards() {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
}

/**
 * Render utility to display a list of doctor cards dynamically in #content.
 * @param {Array} doctors - List of doctor objects
 */
export function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found with the given filters.</p>";
        return;
    }

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

/**
 * Gathers current filter inputs and retrieves matching doctors.
 */
export async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar")?.value.trim() || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    try {
        const doctors = await filterDoctors(name, time, specialty);
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error filtering doctors:", error);
        renderDoctorCards([]);
    }
}

/**
 * Handles patient signup form submission.
 * Attaches directly to the global window for modal form action invocation.
 * @param {Event} event - Form submit event
 */
window.signupPatient = async function (event) {
    if (event) event.preventDefault();

    const name = document.getElementById("signupName")?.value.trim();
    const email = document.getElementById("signupEmail")?.value.trim();
    const password = document.getElementById("signupPassword")?.value;
    const phone = document.getElementById("signupPhone")?.value.trim();
    const address = document.getElementById("signupAddress")?.value.trim();

    const patientData = { name, email, password, phone, address };

    const result = await patientSignup(patientData);

    if (result.success) {
        alert(result.message || "Signup successful! Please log in.");
        const modal = document.getElementById("modal");
        if (modal) {
            modal.style.display = "none";
        }
        window.location.reload();
    } else {
        alert(result.message || "Signup failed. Please try again.");
    }
};

/**
 * Handles patient login form submission.
 * Attaches directly to the global window for modal form action invocation.
 * @param {Event} event - Form submit event
 */
window.loginPatient = async function (event) {
    if (event) event.preventDefault();

    const email = document.getElementById("loginEmail")?.value.trim();
    const password = document.getElementById("loginPassword")?.value;

    try {
        const response = await patientLogin({ email, password });

        if (response.ok) {
            const data = await response.json().catch(() => ({}));
            if (data.token) {
                localStorage.setItem("token", data.token);
            }
            localStorage.setItem("userRole", "loggedPatient");
            alert("Login successful!");
            window.location.href = "loggedPatientDashboard.html";
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert(errorData.message || "Invalid credentials. Please try again.");
        }
    } catch (error) {
        console.error("Login error:", error);
        alert("An error occurred during login. Please try again.");
    }
};