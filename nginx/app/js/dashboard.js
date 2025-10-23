import { getToken } from "./authService.js";


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

  async handleCheck() {
    const ibanInput = document.getElementById("checkIban");
    const resultBox = document.getElementById("checkResult");

    resultBox.innerHTML = "";

    const iban = ibanInput.value.trim();
    if (!iban) {
      resultBox.innerHTML = `<p class="error-message">Please enter IBAN.</p>`;
      return;
    }

    resultBox.innerHTML = `<p class="loading">Checking IBAN...</p>`;

    try {
      const response = await fetch("/api/v1/iban/check", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + getToken(),
        },
        body: JSON.stringify({ "iban": iban }),
      });

      const data = await response.json();

      if (!response.ok) {
        
        resultBox.innerHTML = `
        <div class="error-box">
          <strong>Error:</strong> ${data.message || "Unknown error occurred."}
        </div>`;
        return;
      }


      const { decision, score, reasons } = data;
      let decisionClass = decision === "ALLOW" ? "success" : "error";

      resultBox.innerHTML = `
      <div class="result-box ${decisionClass}">
        <p><strong>IBAN:</strong> ${data.iban}</p>
        <p><strong>Decision:</strong> 
          <span class="decision-${decision.toLowerCase()}">${decision}</span>
        </p>
        <p><strong>Score:</strong> ${score}</p>
        ${
          reasons && reasons.length
            ? `<p><strong>Reasons:</strong><br>${reasons
                .map((r) => `• ${r}`)
                .join("<br>")}</p>`
            : ""
        }
      </div>`;
    } catch (err) {
      
      resultBox.innerHTML = `
      <div class="error-box">
        <strong>Request failed:</strong> ${err.message}
      </div>`;
    }
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
        Reason: ${reason || "N/A"}<br>
        ✅ Thank you for helping keep transactions secure.
      </div>
    `;
    document.getElementById("riskyIban").value = "";
    document.getElementById("reason").value = "";
  }
}
