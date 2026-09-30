const messageBox = document.getElementById("message");
let allMaterials = [];

const pageInfo = {
  dashboard: { eyebrow: "CONSTRUCTION OPERATIONS", title: "Dashboard", subtitle: "Monitor material availability and stock alerts from one place." },
  "register-material": { eyebrow: "MATERIAL MASTER", title: "Register Material", subtitle: "Add a new construction material to the inventory database." },
  "register-supplier": { eyebrow: "SUPPLIER MASTER", title: "Register Supplier", subtitle: "Save supplier contact information for future purchases." },
  "record-purchase": { eyebrow: "STOCK IN", title: "Record Purchase", subtitle: "Record incoming materials and automatically increase stock." },
  "record-usage": { eyebrow: "STOCK OUT", title: "Record Material Usage", subtitle: "Record site consumption and automatically decrease stock." }
};

function showMessage(message, success = true) {
  messageBox.textContent = message;
  messageBox.style.display = "block";
  messageBox.style.background = success ? "#172b4d" : "#b42318";
  clearTimeout(showMessage.timer);
  showMessage.timer = setTimeout(() => messageBox.style.display = "none", 3200);
}
function formDataToUrlEncoded(form) { return new URLSearchParams(new FormData(form)).toString(); }
async function postForm(url, form) {
  const response = await fetch(url, { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: formDataToUrlEncoded(form) });
  const data = await response.json();
  if (!response.ok) throw new Error(data.error || "Request failed");
  return data;
}
function setView(view) {
  if (!pageInfo[view]) view = "dashboard";
  document.querySelectorAll(".view").forEach(section => section.classList.remove("active-view"));
  document.getElementById(`view-${view}`).classList.add("active-view");
  document.querySelectorAll(".nav-item").forEach(item => item.classList.toggle("active", item.dataset.view === view));
  document.getElementById("pageEyebrow").textContent = pageInfo[view].eyebrow;
  document.getElementById("pageTitle").textContent = pageInfo[view].title;
  document.getElementById("pageSubtitle").textContent = pageInfo[view].subtitle;
  window.scrollTo({ top: 0, behavior: "smooth" });
}
function handleRoute() { setView((location.hash || "#dashboard").replace("#", "")); }
async function loadData() {
  try {
    const [materialsResponse, suppliersResponse] = await Promise.all([fetch("/api/materials"), fetch("/api/suppliers")]);
    if (!materialsResponse.ok || !suppliersResponse.ok) throw new Error("Unable to load dashboard data");
    allMaterials = await materialsResponse.json();
    const suppliers = await suppliersResponse.json();
    renderDashboard(allMaterials, suppliers);
    fillMaterialDropdowns(allMaterials);
    fillSupplierDropdown(suppliers);
  } catch (error) { showMessage(error.message, false); }
}
function isLowStock(material) { return Number(material.quantity) < Number(material.minimumStock); }
function renderDashboard(materials, suppliers) {
  document.getElementById("materialCount").textContent = materials.length;
  document.getElementById("supplierCount").textContent = suppliers.length;
  const low = materials.filter(isLowStock);
  document.getElementById("lowStockCount").textContent = low.length;
  document.getElementById("inStockCount").textContent = materials.filter(m => Number(m.quantity) > 0).length;
  const table = document.getElementById("availabilityTable");
  if (!materials.length) table.innerHTML = `<tr><td colspan="5" class="empty-cell">No materials registered yet.</td></tr>`;
  else table.innerHTML = materials.map(m => `<tr><td><strong>${escapeHtml(m.name)}</strong></td><td>${escapeHtml(m.category)}</td><td><strong>${formatNumber(m.quantity)} ${escapeHtml(m.unit)}</strong></td><td>${formatNumber(m.minimumStock)} ${escapeHtml(m.unit)}</td><td><span class="status-badge ${isLowStock(m) ? "status-low" : "status-ok"}">${isLowStock(m) ? "LOW STOCK" : Number(m.quantity) > 0 ? "AVAILABLE" : "NO STOCK"}</span></td></tr>`).join("");
  document.getElementById("lowStockPill").textContent = `${low.length} alert${low.length === 1 ? "" : "s"}`;
  const list = document.getElementById("lowStockList");
  if (!low.length) list.innerHTML = `<div class="alert-empty">All materials are at or above their minimum stock level.</div>`;
  else list.innerHTML = low.map(m => `<div class="alert-item"><div><strong>${escapeHtml(m.name)}</strong><small>Minimum: ${formatNumber(m.minimumStock)} ${escapeHtml(m.unit)}</small></div><strong>${formatNumber(m.quantity)} ${escapeHtml(m.unit)}</strong></div>`).join("");
}
function fillMaterialDropdowns(materials) {
  const options = materials.map(m => `<option value="${m.id}">${escapeHtml(m.name)} — ${formatNumber(m.quantity)} ${escapeHtml(m.unit)}</option>`).join("");
  document.getElementById("purchaseMaterial").innerHTML = options || `<option value="">No materials available</option>`;
  document.getElementById("usageMaterial").innerHTML = options || `<option value="">No materials available</option>`;
}
function fillSupplierDropdown(suppliers) { document.getElementById("purchaseSupplier").innerHTML = suppliers.map(s => `<option value="${s.id}">${escapeHtml(s.name)}</option>`).join("") || `<option value="">No suppliers available</option>`; }
function formatNumber(value) { const number = Number(value); return Number.isFinite(number) ? number.toLocaleString(undefined, { maximumFractionDigits: 2 }) : "0"; }
function escapeHtml(value) { return String(value ?? "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll('"', "&quot;").replaceAll("'", "&#039;"); }
function setToday(selector) { const input = document.querySelector(selector); if (input && !input.value) input.value = new Date().toISOString().split("T")[0]; }
async function handleForm(form, url, successMessage, resetDefaults = null) {
  try { await postForm(url, form); form.reset(); if (resetDefaults) resetDefaults(form); showMessage(successMessage); await loadData(); }
  catch (error) { showMessage(error.message, false); }
}

document.getElementById("materialForm").addEventListener("submit", e => { e.preventDefault(); handleForm(e.target, "/api/materials", "Material added successfully.", form => { form.querySelector('[name="quantity"]').value = "0"; form.querySelector('[name="minimumStock"]').value = "0"; }); });
document.getElementById("supplierForm").addEventListener("submit", e => { e.preventDefault(); handleForm(e.target, "/api/suppliers", "Supplier added successfully."); });
document.getElementById("purchaseForm").addEventListener("submit", e => { e.preventDefault(); handleForm(e.target, "/api/purchases", "Purchase recorded. Stock increased.", form => setToday('#purchaseForm [name="purchaseDate"]')); });
document.getElementById("usageForm").addEventListener("submit", e => { e.preventDefault(); handleForm(e.target, "/api/usage", "Material usage recorded. Stock decreased.", form => setToday('#usageForm [name="usageDate"]')); });
window.addEventListener("hashchange", handleRoute);
setToday('#purchaseForm [name="purchaseDate"]');
setToday('#usageForm [name="usageDate"]');
handleRoute();
loadData();
