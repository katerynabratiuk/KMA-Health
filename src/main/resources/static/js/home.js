document.addEventListener("DOMContentLoaded", function () {
    const authToken = localStorage.getItem('authToken');
    const userRole = localStorage.getItem('userRole');

    const loginRegisterButton = document.getElementById("login-register-button");
    let roleUkrainian;
    if (authToken && loginRegisterButton) {
        loginRegisterButton.textContent = "Вийти";
        loginRegisterButton.href = "#";
        loginRegisterButton.addEventListener('click', function (e) {
            e.preventDefault();
            localStorage.removeItem('authToken');
            localStorage.removeItem('userRole');
            window.location.href = '/ui/public/login';
        });

        const userActionsDiv = document.createElement('div');
        userActionsDiv.className = 'user-actions';
        switch (userRole) {
            case "PATIENT":
                roleUkrainian = "пацієнт";
                break;
            case "DOCTOR":
                roleUkrainian = "лікар";
                break;
            case "LAB_ASSISTANT":
                roleUkrainian = "лаборант";
                break;
            default:
                roleUkrainian = "";
        }
        userActionsDiv.innerHTML = `
            <span class="hello">Привіт! Ви увійшли як ${roleUkrainian}</span>
        `;
        loginRegisterButton.parentElement.appendChild(userActionsDiv);
    }

    const searchType = document.getElementById("search-type");
    const searchInput = document.getElementById("search-input");
    const advancedSearchBtn = document.getElementById("advanced-search-button");
    const advancedSearchContainer = document.getElementById("advanced-search-container");
    const form = document.getElementById("search-container");

    function updatePlaceholder() {
        if (searchType.value === "clinic") {
            searchInput.placeholder = "Enter the name or address";
        } else {
            searchInput.placeholder = "Enter the name or speciality";
        }
    }

    searchType.addEventListener("change", updatePlaceholder);
    updatePlaceholder();

    function generateCityOptions() {
        if (!Array.isArray(cities)) {
            return '<option value="">All Cities</option>';
        }

        const options =
            cities
                .map(c => `<option value="${c.toLowerCase()}">${c}</option>`)
                .join("");

        return `
        <option value="">All Cities</option>
        ${options}
    `;
    }

    function generateSpecialtyOptions() {
        if (!Array.isArray(specialties)) {
            return '<option value="">All Specialties</option>';
        }

        const options =
            specialties
                .map(s => `<option value="${s.toLowerCase()}">${s}</option>`)
                .join("");

        return `
        <option value="">All Specialties</option>
        ${options}
    `;
    }

    function generateClinicFormHTML() {
        return `
        <div class="filter-section">
            <h3>Filter</h3>
            <div class="filter-group">
                <label for="city">City</label>
                <select id="city">
                    ${generateCityOptions()}
                </select>
            </div>
        </div>

        <div class="sort-section">
            <h3>Sort By</h3>
            <div class="sort-group">
                <label><input type="radio" name="sort" value="distance-asc" checked> Distance: Nearest First</label>
                <label><input type="radio" name="sort" value="distance-dsc"> Distance: Farthest First</label>
            </div>
        </div>
    `;
    }

    function generateDoctorFormHTML() {
        return `
        <div class="filter-section">
            <h3>Filter</h3>
            <div class="filter-group">
                <label for="doctor-type">Doctor Type</label>
                <select id="doctor-type">
                    ${generateSpecialtyOptions()}
                </select>
            </div>

            <div class="filter-group">
                <label for="city">City</label>
                <select id="city">
                    ${generateCityOptions()}
                </select>
            </div>
        </div>

        <div class="sort-section">
            <h3>Sort By</h3>
            <div class="sort-group">
                <label><input type="radio" name="sort" value="rating-dsc" checked> Rating: High to Low</label>
                <label><input type="radio" name="sort" value="rating-asc"> Rating: Low to High</label>
                <label><input type="radio" name="sort" value="distance-asc"> Distance: Nearest First</label>
                <label><input type="radio" name="sort" value="distance-dsc"> Distance: Farthest First</label>

                <input type="hidden" name="sortBy.param" id="hidden-sort-param" value="rating">
                <input type="hidden" name="sortBy.direction" id="hidden-sort-direction" value="asc">
            </div>
        </div>
    `;
    }

    function renderAdvancedForm() {
        if (searchType.value === "clinic") {
            advancedSearchContainer.innerHTML = generateClinicFormHTML();
        } else {
            advancedSearchContainer.innerHTML = generateDoctorFormHTML();
        }
    }

    let isVisible = false;

    advancedSearchBtn.addEventListener("click", () => {
        isVisible = !isVisible;

        if (isVisible) {
            renderAdvancedForm();
            advancedSearchContainer.classList.remove("hidden");
            advancedSearchBtn.textContent = "Hide Advanced Search";
        } else {
            advancedSearchContainer.classList.add("hidden");
            advancedSearchBtn.textContent = "Advanced Search";
        }
    });

    searchType.addEventListener("change", () => {
        updatePlaceholder();
        if (isVisible) {
            renderAdvancedForm();
        }
        // Automatically submit the form to load all results for the selected type
        form.submit();
    });

    let userLatInput = document.getElementById("userLat");
    let userLonInput = document.getElementById("userLon");

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (pos) => {
                userLatInput.value = pos.coords.latitude;
                userLonInput.value = pos.coords.longitude;
            },
            () => {
                console.warn("User denied geolocation — using default (0,0)");
                userLatInput.value = 0;
                userLonInput.value = 0;
            }
        );
    } else {
        userLatInput.value = 0;
        userLonInput.value = 0;
    }

    form.addEventListener("submit", function (e) {
        const doctorTypeSelect = document.getElementById("doctor-type");
        const citySelect = document.getElementById("city");
        const sortRadio = advancedSearchContainer.querySelector('input[name="sort"]:checked');

        const hiddenDoctorType = document.getElementById("hidden-doctor-type");
        const hiddenCity = document.getElementById("hidden-city");
        const hiddenSort = document.getElementById("hidden-sort");

        hiddenDoctorType.value = doctorTypeSelect && doctorTypeSelect.value !== "" ? doctorTypeSelect.value : null;
        hiddenCity.value = citySelect && citySelect.value !== "" ? citySelect.value : null;
        hiddenSort.value = sortRadio ? sortRadio.value : "rating-asc";
    });

    const appointmentButtons = document.querySelectorAll('.appointment-button');
    appointmentButtons.forEach(button => {
        button.addEventListener('click', function () {
            if (!authToken) {
                alert('Будь ласка, увійдіть в систему для запису на прийом');
                window.location.href = '/ui/public/login';
            } else {
                console.log('Book appointment - implement this functionality');
            }
        });
    });
});