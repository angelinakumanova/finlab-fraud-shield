import { login } from "./authService.js";
import { Dashboard } from "./dashboard.js";
import { showError, clearError, setLoading } from "./uiHelpers.js";

export class CorporateLoginForm {
  constructor() {
    this.form = document.getElementById("loginForm");
    this.usernameInput = document.getElementById("username");
    this.passwordInput = document.getElementById("password");
    this.passwordToggle = document.getElementById("passwordToggle");
    this.submitButton = this.form.querySelector(".login-btn");
    this.successMessage = document.getElementById("successMessage");

    this.init();
  }

  init() {
    this.form.addEventListener("submit", (e) => this.handleSubmit(e));
    this.usernameInput.addEventListener("input", clearError);
    this.passwordInput.addEventListener("input", clearError);
    this.passwordToggle.addEventListener("click", () => this.togglePassword());
  }

  togglePassword() {
    const type = this.passwordInput.type === "password" ? "text" : "password";
    this.passwordInput.type = type;
    const icon = this.passwordToggle.querySelector(".toggle-icon");
    icon.classList.toggle("show-password", type === "text");
  }

  async handleSubmit(e) {
    e.preventDefault();
    setLoading(this.submitButton, true);

    try {
      const username = this.usernameInput.value;
      const password = this.passwordInput.value;
      await login(username, password);
      this.showSuccess();
    } catch (error) {
      showError(error);
    } finally {
      setLoading(this.submitButton, false);
    }
  }

  showSuccess() {
  const loginHeader = document.querySelector('.login-header');
  loginHeader.classList.add('hidden');
  this.form.style.display = "none";
  this.successMessage.classList.add("show");

  setTimeout(() => {
    const loginContainer = document.getElementById('login-container');
    const dashboard = document.getElementById('dashboard');

    loginContainer.classList.add("hidden");
    dashboard.classList.remove("hidden");

    new Dashboard();
  }, 2500);
}
}
