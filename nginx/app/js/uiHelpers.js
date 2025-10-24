export function showError(message) {
  const formGroup = document.querySelector(".form-group");
  const errorElement = document.getElementById("authError");
  formGroup.classList.add("error");
  errorElement.textContent = message;
  errorElement.classList.add("show");
}

export function clearError() {
  const formGroup = document.querySelector(".form-group");
  const errorElement = document.getElementById("authError");
  formGroup.classList.remove("error");
  errorElement.classList.remove("show");
  setTimeout(() => {
    errorElement.textContent = "";
  }, 300);
}

export function setLoading(button, loading) {
  button.classList.toggle("loading", loading);
  button.disabled = loading;
}
