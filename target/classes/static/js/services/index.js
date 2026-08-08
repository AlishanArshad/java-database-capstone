/**
 * Role Selection Logic for index.html
 */

document.addEventListener("DOMContentLoaded", () => {
    const adminBtn = document.getElementById("adminBtn");
    const doctorBtn = document.getElementById("doctorBtn");
    const patientBtn = document.getElementById("patientBtn");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => selectRole("admin"));
    }

    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => selectRole("doctor"));
    }

    if (patientBtn) {
        patientBtn.addEventListener("click", () => selectRole("patient"));
    }
});

/**
 * Stores role selection and routes user to target dashboard.
 * Supports both local file testing (file://) and Spring Boot server execution (http://).
 * @param {string} role - Selected role ('admin', 'doctor', or 'patient')
 */
export function selectRole(role) {
    localStorage.setItem("userRole", role);

    const isLocalFile = window.location.protocol === "file:";

    if (role === "admin") {
        window.location.href = isLocalFile
            ? "../templates/admin/adminDashboard.html"
            : "/admin/dashboard";
    } else if (role === "doctor") {
        window.location.href = isLocalFile
            ? "../templates/doctor/doctorDashboard.html"
            : "/doctor/dashboard";
    } else if (role === "patient") {
        window.location.href = isLocalFile
            ? "./pages/patientDashboard.html"
            : "/pages/patientDashboard.html";
    }
}

window.selectRole = selectRole;