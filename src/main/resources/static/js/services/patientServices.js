import { API_BASE_URL } from "../config/config.js";

// Base Endpoint for Patient API
const PATIENT_API = API_BASE_URL + '/patient';

/**
 * Registers a new patient.
 * @param {Object} data - Patient details (name, email, password, etc.)
 * @returns {Promise<{success: boolean, message: string}>} Structured response
 */
export async function patientSignup(data) {
    try {
        const response = await fetch(`${PATIENT_API}/signup`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const result = await response.json().catch(() => ({}));

        if (response.ok) {
            return { success: true, message: result.message || "Signup successful!" };
        } else {
            return { success: false, message: result.message || "Signup failed. Please try again." };
        }
    } catch (error) {
        console.error("Error during patient signup:", error);
        return { success: false, message: "Network error occurred during signup." };
    }
}

/**
 * Authenticates a patient during login.
 * @param {Object} data - Login credentials ({ email, password })
 * @returns {Promise<Response>} Full fetch response
 */
export async function patientLogin(data) {
    try {
        // Development verification log
        console.log("Attempting patient login with payload:", { email: data.email });

        const response = await fetch(`${PATIENT_API}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        return response;
    } catch (error) {
        console.error("Error during patient login:", error);
        throw error;
    }
}

/**
 * Fetches profile details for the currently logged-in patient.
 * @param {string} token - Authentication token
 * @returns {Promise<Object|null>} Patient profile object or null on failure
 */
export async function getPatientData(token) {
    try {
        const response = await fetch(`${PATIENT_API}/me`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch patient details. Status: ${response.status}`);
        }

        const patientData = await response.json();
        return patientData;
    } catch (error) {
        console.error("Error fetching patient data:", error);
        return null;
    }
}

/**
 * Fetches patient appointments dynamically for both Patient and Doctor roles.
 * @param {string|number} id - Unique identifier (Patient ID)
 * @param {string} token - Authentication token
 * @param {string} user - Requester role ("patient" or "doctor")
 * @returns {Promise<Array|null>} Array of appointment records or null
 */
export async function getPatientAppointments(id, token, user) {
    try {
        const url = `${PATIENT_API}/appointments?id=${encodeURIComponent(id)}&user=${encodeURIComponent(user)}`;
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch appointments. Status: ${response.status}`);
        }

        const appointments = await response.json();
        return appointments;
    } catch (error) {
        console.error("Error fetching patient appointments:", error);
        return null;
    }
}

/**
 * Filters appointments based on status condition and patient name.
 * @param {string} condition - Status condition (e.g., "pending", "consulted")
 * @param {string} name - Patient name query
 * @param {string} token - Authentication token
 * @returns {Promise<Array>} Filtered appointments array or empty array on failure
 */
export async function filterAppointments(condition = "", name = "", token) {
    try {
        const queryParams = new URLSearchParams();
        if (condition) queryParams.append("condition", condition);
        if (name) queryParams.append("name", name);

        const url = `${PATIENT_API}/appointments/filter?${queryParams.toString()}`;
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`Filter request failed with status: ${response.status}`);
        }

        const filteredAppointments = await response.json();
        return filteredAppointments;
    } catch (error) {
        console.error("Error filtering appointments:", error);
        alert("Unable to filter appointments. Please try again.");
        return [];
    }
}