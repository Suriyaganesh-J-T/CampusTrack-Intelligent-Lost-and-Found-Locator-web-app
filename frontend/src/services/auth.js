export function saveToken(token) {
    localStorage.setItem("jwt", token);
}
export function clearToken() {
    localStorage.removeItem("jwt");
}
export function getToken() {
    return localStorage.getItem("jwt");
}
