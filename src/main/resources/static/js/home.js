document.addEventListener("DOMContentLoaded", function () {
    const authToken = localStorage.getItem('authToken');
    const userRole = localStorage.getItem('userRole');
    
    const loginRegisterButton = document.getElementById("login-register-button");
    if (authToken && loginRegisterButton) {
        loginRegisterButton.textContent = "Вийти";
        loginRegisterButton.href = "#";
        loginRegisterButton.addEventListener('click', function(e) {
            e.preventDefault();
            localStorage.removeItem('authToken');
            localStorage.removeItem('userRole');
            window.location.href = '/ui/public/login';
        });
        
        const userActionsDiv = document.createElement('div');
        userActionsDiv.className = 'user-actions';
        userActionsDiv.innerHTML = `
            <span class="hello">Привіт! Ви увійшли як ${userRole}</span>
        `;
        loginRegisterButton.parentElement.appendChild(userActionsDiv);
    }
    
    const searchType = document.getElementById("search-type");
    const searchInput = document.getElementById("search-input");
    const advancedSearchBtn = document.getElementById("advanced-search-button");
    const advancedSearchContainer = document.getElementById("advanced-search-container");

    function updatePlaceholder() {
        if (searchType.value === "clinic") {
            searchInput.placeholder = "Enter the name or address";
        } else {
            searchInput.placeholder = "Enter the name or speciality";
        }
    }

    searchType.addEventListener("change", updatePlaceholder);
    updatePlaceholder();

    const doctorFormHTML = `
        <div class="filter-section">
            <h3>Filter</h3>
            <div class="filter-group">
                <label for="doctor-type">Doctor Type</label>
                <select id="doctor-type">
                    <option value="">All Specialties</option>
                    <option value="cardiologist">Cardiologist</option>
                    <option value="pediatrics">Pediatrician</option>
                    <option value="dermatology">Dermatologist</option>
                    <option value="surgery">Surgeon</option>
                </select>
            </div>

            <div class="filter-group">
                <label for="city">City</label>
                <select id="city">
                    <option value="">All Cities</option>
                    <option value="kyiv">Kyiv</option>
                    <option value="lviv">Lviv</option>
                    <option value="kharkiv">Kharkiv</option>
                    <option value="odesa">Odesa</option>
                </select>
            </div>
        </div>

        <div class="sort-section">
            <h3>Sort By</h3>
            <div class="sort-group">
                <label><input type="radio" name="sort" value="rating-desc" checked> Rating: High to Low</label>
                <label><input type="radio" name="sort" value="rating-asc"> Rating: Low to High</label>
                <label><input type="radio" name="sort" value="distance-asc"> Distance: Nearest First</label>
                <label><input type="radio" name="sort" value="distance-desc"> Distance: Farthest First</label>
            </div>
        </div>
    `;

    const clinicFormHTML = `
        <div class="filter-section">
            <h3>Filter</h3>
            <div class="filter-group">
                <label for="city">City</label>
                <select id="city">
                    <option value="">All Cities</option>
                    <option value="kyiv">Kyiv</option>
                    <option value="lviv">Lviv</option>
                    <option value="kharkiv">Kharkiv</option>
                    <option value="odesa">Odesa</option>
                </select>
            </div>
        </div>

        <div class="sort-section">
            <h3>Sort By</h3>
            <div class="sort-group">
                <label><input type="radio" name="sort" value="distance-asc" checked> Distance: Nearest First</label>
                <label><input type="radio" name="sort" value="distance-desc"> Distance: Farthest First</label>
            </div>
        </div>
    `;

    function renderAdvancedForm() {
        if (searchType.value === "clinic") {
            advancedSearchContainer.innerHTML = clinicFormHTML;
        } else {
            advancedSearchContainer.innerHTML = doctorFormHTML;
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
    });

    const form = document.getElementById("search-container");
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

    form.addEventListener("submit", (e) => {
        const doctorTypeSelect = document.getElementById("doctor-type");
        const citySelect = document.getElementById("city");
        const sortRadio = advancedSearchContainer.querySelector('input[name="sort"]:checked');

        document.getElementById("hidden-doctor-type").value = doctorTypeSelect ? doctorTypeSelect.value : "";
        document.getElementById("hidden-city").value = citySelect ? citySelect.value : "";
        document.getElementById("hidden-sort").value = sortRadio ? sortRadio.value : "";
    });
    
    const appointmentButtons = document.querySelectorAll('.appointment-button');
    appointmentButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (!authToken) {
                alert('Будь ласка, увійдіть в систему для запису на прийом');
                window.location.href = '/ui/public/login';
            } else {
                console.log('Book appointment - implement this functionality');
            }
        });
    });
});