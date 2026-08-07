import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// Global / Module Variables
let selectedDate = getTodayDateString();
let token = localStorage.getItem("token");
let patientName = "null";

document.addEventListener("DOMContentLoaded", () => {
    // Re-fetch token on load
    token = localStorage.getItem("token");

    const searchBar = document.getElementById("searchBar");
    const todayButton = document.getElementById("todayAppointmentsBtn") || document.getElementById("todayButton");
    const datePicker = document.getElementById("datePicker");

    // Initialize Date Picker to today's date
    if (datePicker) {
        datePicker.value = selectedDate;
        datePicker.addEventListener("change", (e) => {
            selectedDate = e.target.value || getTodayDateString();
            loadAppointments();
        });
    }

    // "Today's Appointments" button event binding
    if (todayButton) {
        todayButton.addEventListener("click", () => {
            selectedDate = getTodayDateString();
            if (datePicker) {
                datePicker.value = selectedDate;
            }
            loadAppointments();
        });
    }

    // Search bar input listener
    if (searchBar) {
        searchBar.addEventListener("input", (e) => {
            const val = e.target.value.trim();
            patientName = val === "" ? "null" : val;
            loadAppointments();
        });
    }

    // Initial render on page load
    loadAppointments();
});

/**
 * Fetches and displays appointments dynamically based on selected date and search filter.
 */
export async function loadAppointments() {
    const tableBody = document.getElementById("patientTableBody");
    if (!tableBody) return;

    // Clear existing content
    tableBody.innerHTML = "";

    try {
        token = localStorage.getItem("token");
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `
        <tr>
          <td colspan="5" class="noPatientRecord">No Appointments found for today</td>
        </tr>
      `;
            return;
        }

        // Render each appointment row
        appointments.forEach((appointment) => {
            const row = createPatientRow(appointment);
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error("Error loading appointments:", error);
        tableBody.innerHTML = `
      <tr>
        <td colspan="5" class="noPatientRecord" style="color: #d9534f;">Failed to load appointments. Please try again.</td>
      </tr>
    `;
    }
}

/**
 * Utility function to format current date as YYYY-MM-DD.
 * @returns {string} Date string
 */
function getTodayDateString() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}