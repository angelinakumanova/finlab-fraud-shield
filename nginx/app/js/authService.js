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
        : "Unexpected server error.");
    throw new Error(errorMsg);
  }

  const result = await response.json();
  
  const token = result.token;

  if (!token) throw new Error("No token received from server.");

  JWTToken = token;
  return { token };
}

export function getToken() {
  return JWTToken;
}
