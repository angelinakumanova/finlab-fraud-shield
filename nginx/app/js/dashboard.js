// dashboard.js
export class Dashboard {
  constructor() {
    this.checkPanel = document.getElementById("checkPanel");
    this.reportPanel = document.getElementById("reportPanel");
    this.init();
  }

  init() {
    document
      .getElementById("checkIbanBtn")
      .addEventListener("click", () => this.showPanel(this.checkPanel));
    document
      .getElementById("reportIbanBtn")
      .addEventListener("click", () => this.showPanel(this.reportPanel));

    document
      .getElementById("checkBtn")
      .addEventListener("click", () => this.handleCheck());
    document
      .getElementById("reportBtn")
      .addEventListener("click", () => this.handleReport());
  }

  showPanel(panelToShow) {
    [this.checkPanel, this.reportPanel].forEach((panel) => {
      if (panel === panelToShow) {
        panel.style.display = "block";
        panel.style.opacity = 0;
        requestAnimationFrame(() => {
          panel.style.transition = "opacity 0.4s ease";
          panel.style.opacity = 1;
        });
      } else {
        panel.style.opacity = 0;
        setTimeout(() => (panel.style.display = "none"), 300);
      }
    });
  }

  handleCheck() {
    const ibans = document
      .getElementById("ibanList")
      .value.trim()
      .split(/\n+/);
    const resultBox = document.getElementById("checkResult");
    if (!ibans[0]) {
      resultBox.innerHTML = `<p class='error-message'>Please enter at least one IBAN.</p>`;
      return;
    }
    let html = `<div class='result-box'><strong>Results:</strong><br>`;
    ibans.forEach((iban) => {
      const clean = iban.trim();
      const valid = /^[A-Z]{2}\d{2}[A-Z0-9]{10,30}$/.test(clean);
      html += `${clean} → ${valid ? "✅ Valid" : "❌ Invalid"}<br>`;
    });
    html += `</div>`;
    resultBox.innerHTML = html;
  }

  handleReport() {
    const iban = document.getElementById("riskyIban").value.trim();
    const reason = document.getElementById("reason").value.trim();
    const res = document.getElementById("reportResult");

    if (!iban) {
      res.innerHTML = `<p class='error-message'>Please enter an IBAN to report.</p>`;
      return;
    }

    res.innerHTML = `
      <div class='result-box'>
        🚨 <strong>Report submitted</strong> for IBAN <b>${iban}</b><br>
        Reason: ${reason || 'N/A'}<br>
        ✅ Thank you for helping keep transactions secure.
      </div>
    `;
    document.getElementById("riskyIban").value = "";
    document.getElementById("reason").value = "";
  }
}
