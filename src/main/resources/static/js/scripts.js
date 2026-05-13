// --- FUNCIONES DE NAVEGACIÓN DENTRO DEL MODAL ---
function mostrarRegistro() {
    const loginSec = document.getElementById('loginSection');
    const registerSec = document.getElementById('registerSection');
    if (loginSec && registerSec) {
        loginSec.style.display = 'none';
        registerSec.style.display = 'block';
    }
}

function mostrarLogin() {
    const loginSec = document.getElementById('loginSection');
    const registerSec = document.getElementById('registerSection');
    if (loginSec && registerSec) {
        registerSec.style.display = 'none';
        loginSec.style.display = 'block';
    }
}

// --- GESTIÓN DE SESIÓN (LOGIN / LOGOUT / VERIFICACIÓN) ---
function verificarSesion() {
    const usuario = localStorage.getItem('usuarioLogueado');
    const btnLogin = document.getElementById('btnInicioSesion');
    const userProfile = document.getElementById('userProfile');
    const userInitial = document.getElementById('userInitial');
    const userNameDisplay = document.getElementById('userNameDisplay');

    if (usuario) {
        if (btnLogin) btnLogin.style.display = 'none';
        if (userProfile) {
            userProfile.style.display = 'block';
            if (userInitial) userInitial.innerText = usuario.charAt(0).toUpperCase();
            if (userNameDisplay) userNameDisplay.innerText = usuario;
        }
    } else {
        if (btnLogin) btnLogin.style.display = 'block';
        if (userProfile) userProfile.style.display = 'none';
    }
}

function logout() {
    localStorage.removeItem('usuarioLogueado');
    alert("Cerrando sesión en NEXO WEAR...");
    verificarSesion();
    window.location.href = "/"; 
}

// --- INICIALIZACIÓN Y EVENTOS ---
document.addEventListener("DOMContentLoaded", function() {
    verificarSesion();

    // 1. Manejo del Login
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const user = document.getElementById('usuario').value;
            const pass = document.getElementById('password').value;

            fetch(`/auth/login?usuario=${encodeURIComponent(user)}&password=${encodeURIComponent(pass)}`, { 
                method: 'POST' 
            })
            .then(response => response.text())
            .then(data => {
                if (data.trim() === "success") {
                    localStorage.setItem('usuarioLogueado', user);
                    alert("Bienvenido, " + user);
                    
                    // Cerrar modal
                    const modalEl = document.getElementById('loginModal');
                    const modalInstance = bootstrap.Modal.getInstance(modalEl);
                    if (modalInstance) modalInstance.hide();
                    
                    verificarSesion();
                } else {
                    alert("Error: Usuario o contraseña incorrectos.");
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("Hubo un problema al conectar con el servidor.");
            });
        });
    }

    // 2. Manejo del Registro
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const newUser = document.getElementById('newUser').value;
            const newPass = document.getElementById('newPassword').value; // Usamos el ID corregido

            fetch(`/auth/registrar?username=${encodeURIComponent(newUser)}&password=${encodeURIComponent(newPass)}`, { 
                method: 'POST' 
            })
            .then(response => response.text())
            .then(data => {
                if (data.trim() === "success") {
                    alert("¡Cuenta creada con éxito para " + newUser + "! Ya puedes iniciar sesión.");
                    mostrarLogin();
                    registerForm.reset();
                } else {
                    alert("Error al crear la cuenta. Es posible que el nombre de usuario ya esté en uso.");
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("Error de conexión al registrar.");
            });
        });
    }

    // 3. Reset del Modal al cerrar
    const myModalEl = document.getElementById('loginModal');
    if (myModalEl) {
        myModalEl.addEventListener('hidden.bs.modal', function () {
            mostrarLogin();
            if (loginForm) loginForm.reset();
            if (registerForm) registerForm.reset();
        });
    }
});
async function agregarAlCarrito(productoId) {
    const username = localStorage.getItem('usuarioLogueado');

    // REGLA: Si no hay usuario en localStorage, no dejamos agregar
    if (!username) {
        alert("Debes iniciar sesión para agregar productos al carrito.");
        const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
        loginModal.show();
        return;
    }

    try {
        const response = await fetch(`/api/carrito/agregar?username=${username}&productoId=${productoId}`, {
            method: 'POST'
        });

        if (response.ok) {
            alert("Producto añadido a tu cuenta con éxito");
            actualizarVistaCarrito(); // Refresca el panel lateral
        }
    } catch (error) {
        console.error("Error al conectar con el servidor", error);
    }
}

async function actualizarVistaCarrito() {
    const username = localStorage.getItem('usuarioLogueado');
    if (!username) return;

    const response = await fetch(`/api/carrito/listar?username=${username}`);
    const items = await response.json();
    
    const listaContainer = document.getElementById('listaCarrito');
    const badge = document.getElementById('carritoBadge');
    const totalLabel = document.getElementById('totalCarrito');

    if (items.length === 0) {
        listaContainer.innerHTML = '<p class="text-center text-muted">Tu carrito está vacío.</p>';
        badge.style.display = 'none';
        totalLabel.innerText = "S/ 0.00";
        return;
    }

    // Actualizar Badge
    badge.innerText = items.length;
    badge.style.display = 'block';

    // Renderizar items
    listaContainer.innerHTML = '';
    let total = 0;
    items.forEach(item => {
    total += item.producto.precio;
    listaContainer.innerHTML += `
        <div class="cart-item d-flex justify-content-between align-items-center mb-3 p-3 text-white">
            <div class="d-flex align-items-center gap-3">
                <i class="bi bi-box-seam text-primary fs-4"></i>
                <div>
                    <h6 class="m-0 fw-bold">${item.producto.nombre}</h6>
                    <small class="text-muted">S/ ${item.producto.precio.toFixed(2)}</small>
                </div>
            </div>
            <button class="btn btn-sm btn-outline-danger border-0" onclick="eliminarDelCarrito(${item.id})">
                <i class="bi bi-trash"></i>
            </button>
        </div>
    `;
});
    totalLabel.innerText = `S/ ${total.toFixed(2)}`;
}

// Para que el carrito se cargue apenas el usuario entre a la página
document.addEventListener('DOMContentLoaded', actualizarVistaCarrito);
// --- WHATSAPP ---
function enviarWhatsApp() {
    const telefono = "51924488934"; 
    const nombre = document.getElementById('nombre')?.value || "";
    const mensaje = document.getElementById('mensaje')?.value || "";
    const asunto = document.getElementById('asunto')?.value || "Consulta General";

    if (!nombre.trim() || !mensaje.trim()) {
        alert("Por favor, completa los campos obligatorios.");
        return;
    }

    const texto = `*NEXO WEAR*%0A*De:* ${nombre}%0A*Asunto:* ${asunto}%0A*Mensaje:* ${mensaje}`;
    const url = `https://wa.me/${telefono}?text=${texto}`;
    window.open(url, '_blank');
}

async function eliminarDelCarrito(id) {
    if (!confirm("¿Estás seguro de quitar este producto?")) return;

    try {
        const response = await fetch(`/api/carrito/eliminar/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // Refrescamos la vista para que el producto desaparezca visualmente
            actualizarVistaCarrito();
        } else {
            alert("No se pudo eliminar el producto.");
        }
    } catch (error) {
        console.error("Error al eliminar:", error);
    }
}