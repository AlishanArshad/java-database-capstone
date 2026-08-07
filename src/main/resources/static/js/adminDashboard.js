import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

document.addEventListener("DOMContentLoaded", () => {
    // 1. Initial Doctor Data Load
    loadDoctorCards();

    // 2. Attach Event Listeners for Search & Filters
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime") || document.getElementById("timeSort");
    const filterSpecialty = document.getElementById("filterSpecialty") || document.getElementById("specialtyFilter");

    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }
    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }
    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }

    // 3. Delegate Click Event for Add Doctor Button (handles dynamic header rendering)
    document.addEventListener("click", (e) => {
        if (e.target && e.target.id === "addDocBtn") {
            openModal("addDoctor");
        }
    });
});

/**
 * Fetches all doctors from backend service and renders them.
 */
export async function loadDoctorCards() {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
}

/**
 * Utility function to render doctor cards into the content container.
 * @param {Array} doctors - Array of doctor objects to display
 */
export function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = `<p class="noPatientRecord">No doctors found</p>`;
        return;
    }

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

/**
 * Event handler triggered on search input or filter changes.
 */
async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar")?.value.trim() || "";
    const time = (document.getElementById("filterTime") || document.getElementById("timeSort"))?.value || "";
    const specialty = (document.getElementById("filterSpecialty") || document.getElementById("specialtyFilter"))?.value || "";

    const filteredDoctors = await filterDoctors(name, time, specialty);
    renderDoctorCards(filteredDoctors);
}

/**
 * Processes "Add Doctor" form submission, gathers input, and sends POST request.
 * @param {Event} event - Form submission event
 */
export async function adminAddDoctor(event) {
    if (event) event.preventDefault();

    const name = document.getElementById("docName")?.value.trim();
    const specialty = document.getElementById("docSpecialty")?.value;
    const email = document.getElementById("docEmail")?.value.trim();
    const password = document.getElementById("docPassword")?.value;
    const mobile = document.getElementById("docMobile")?.value.trim();

    // Collect selected availability checkboxes
    const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
    const availability = Array.from(availabilityCheckboxes).map((cb) => cb.value);

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Authentication token missing. Please log in again.");
        return;
    }

    const doctorData = {
        name,
        specialty,
        email,
        password,
        mobile,
        availability,
    };

    const result = await saveDoctor(doctorData, token);

    if (result.success) {
        alert(result.message || "Doctor added successfully!");

        // Close the modal
        const modal = document.getElementById("modal");
        if (modal) {
            modal.style.display = "none";
        }

        // Refresh doctor list
        await loadDoctorCards();
    } else {
        alert(result.message || "Failed to add doctor. Please try again.");
    }
}

// Make adminAddDoctor globally available for dynamic modal form submission
window.adminAddDoctor = adminAddDoctor;