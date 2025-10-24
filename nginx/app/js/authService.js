let JWTToken = null;

export async function login(username, password) {
  const options = {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  };

  //http://localhost:8081
  const response = await fetch("/api/v1/auth/login", options);

  if (!response.ok) {
    const errorMsg =
      (response.status === 401
        ? "Invalid username or password."
        : "Unexpected server error. Contact the IT administrator.");
    throw new Error(errorMsg);
  }

  const result = await response.json();
  
  const token = result.token;

  if (!token) throw new Error("No token received from server.");

  JWTToken = token;
  return { token };
}

document.getElementById("logoutBtn").addEventListener("click", async function () {
  try {
    
    if (!JWTToken) {
      alert("No active session found.");
      return;
    }
    const response = await fetch("/api/v1/auth/logout", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${JWTToken}`
      }
    });

    if (!response.ok) {
      throw new Error(`Logout failed: ${response.status}`);
    }

    location.reload();

    alert("You have been logged out successfully.");
  } catch (error) {
    console.error("Logout error:", error);
    alert("An error occurred while logging out. Please try again.");
  }
});


export function getToken() {
  return JWTToken;
}
