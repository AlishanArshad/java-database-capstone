import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Retrieves all doctor records from the backend.
 * @returns {Promise<Array>} Array of doctor objects or empty array on failure.
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);
        if (!response.ok) {
            throw new Error(`Failed to fetch doctors. Status: ${response.status}`);
        }
        const doctors = await response.json();
        return doctors;
    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

/**
 * Deletes a doctor by ID using administrative authentication.
 * @param {string|number} id - Doctor unique identifier
 * @param {string} token - Admin auth token
 * @returns {Promise<{success: boolean, message: string}>} Structured response
 */
export async function deleteDoctor(id, token) {
    try {
        const url = `${DOCTOR_API}/${id}?token=${encodeURIComponent(token)}`;
        const response = await fetch(url, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        const data = await response.json().catch(() => ({}));

        if (response.ok) {
            return { success: true, message: data.message || "Doctor deleted successfully." };
        } else {
            return { success: false, message: data.message || "Failed to delete doctor." };
        }
    } catch (error) {
        console.error(`Error deleting doctor ID ${id}:`, error);
        return { success: false, message: "Network error occurred while deleting doctor." };
    }
}

/**
 * Saves a new doctor to the backend system.
 * @param {Object} doctor - Doctor details object
 * @param {string} token - Admin auth token
 * @returns {Promise<{success: boolean, message: string, data?: Object}>} Structured response
 */
export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json().catch(() => ({}));

        if (response.ok) {
            return { success: true, message: "Doctor added successfully.", data };
        } else {
            return { success: false, message: data.message || "Failed to save doctor." };
        }
    } catch (error) {
        console.error("Error saving doctor:", error);
        return { success: false, message: "Network error occurred while saving doctor." };
    }
}

/**
 * Filters doctor records based on name, time availability, and specialty.
 * @param {string} name - Name search query
 * @param {string} time - Time availability filter
 * @param {string} specialty - Medical specialty filter
 * @returns {Promise<Array>} List of matching doctor objects or empty array
 */
export async function filterDoctors(name = "", time = "", specialty = "") {
    try {
        const queryParams = new URLSearchParams();
        if (name) queryParams.append("name", name);
        if (time) queryParams.append("time", time);
        if (specialty) queryParams.append("specialty", specialty);

        const url = `${DOCTOR_API}/filter?${queryParams.toString()}`;
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`Filter query failed with status: ${response.status}`);
        }

        const filteredDoctors = await response.json();
        return filteredDoctors;
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Unable to apply doctor filters. Please try again.");
        return [];
    }
}