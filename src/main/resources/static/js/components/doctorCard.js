import { showBookingOverlay, getPatientData } from "./modals.js";

/**
 * Creates a dynamic doctor card component with role-based action buttons.
 * @param {Object} doctor - Doctor details (id, name, specialty, email, availability)
 * @returns {HTMLElement} The complete doctor card element
 */
export function createDoctorCard(doctor) {
    // 1. Create Main Card Container
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // 2. Fetch User Role
    const role = localStorage.getItem("userRole");

    // 3. Create Doctor Info Section
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name || doctor.doctorName || "Dr. Unknown";

    const specialization = document.createElement("p");
    specialization.classList.add("specialty");
    specialization.textContent = `Specialty: ${doctor.specialty || doctor.specialization || "General"}`;

    const email = document.createElement("p");
    email.classList.add("email");
    email.textContent = `Email: ${doctor.email || "N/A"}`;

    const availability = document.createElement("p");
    availability.classList.add("availability");
    const availabilityText = Array.isArray(doctor.availability)
        ? doctor.availability.join(", ")
        : doctor.availability || "Not specified";
    availability.textContent = `Availability: ${availabilityText}`;

    // Append elements to info container
    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // 4. Create Button Container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // 5. Conditionally Add Buttons Based on Role
    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.classList.add("btn-delete");
        removeBtn.textContent = "Delete";

        removeBtn.addEventListener("click", async () => {
            const confirmed = confirm(`Are you sure you want to delete ${name.textContent}?`);
            if (!confirmed) return;

            const token = localStorage.getItem("token");
            try {
                const response = await fetch(`/api/admin/doctors/${doctor.id}`, {
                    method: "DELETE",
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                });

                if (response.ok) {
                    card.remove();
                } else {
                    alert("Failed to delete doctor. Please try again.");
                }
            } catch (error) {
                console.error("Error deleting doctor:", error);
                alert("An error occurred while deleting the doctor.");
            }
        });

        actionsDiv.appendChild(removeBtn);
    } else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.classList.add("btn-book");
        bookNow.textContent = "Book Now";

        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });

        actionsDiv.appendChild(bookNow);
    } else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.classList.add("btn-book");
        bookNow.textContent = "Book Now";

        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Session invalid. Please log in again.");
                return;
            }

            try {
                const patientData = await getPatientData(token);
                if (typeof showBookingOverlay === "function") {
                    showBookingOverlay(e, doctor, patientData);
                }
            } catch (error) {
                console.error("Error fetching patient data for booking:", error);
                alert("Unable to initiate booking. Please try again.");
            }
        });

        actionsDiv.appendChild(bookNow);
    }

    // 6. Final Assembly
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}