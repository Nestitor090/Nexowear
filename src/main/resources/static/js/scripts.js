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
    const rol = localStorage.getItem('usuarioRol'); // 🌟 Recuperamos el rol de la sesión

    const btnLogin = document.getElementById('btnInicioSesion');
    const userProfile = document.getElementById('userProfile');
    const userInitial = document.getElementById('userInitial');
    const userNameDisplay = document.getElementById('userNameDisplay');
    const navAdminLink = document.getElementById('navAdminLink'); // 🌟 Capturamos el link del Navbar

    if (usuario) {
        if (btnLogin) btnLogin.style.display = 'none';
        if (userProfile) {
            userProfile.style.display = 'block';
            if (userInitial) userInitial.innerText = usuario.charAt(0).toUpperCase();
            if (userNameDisplay) userNameDisplay.innerText = usuario;
        }

        // 🌟 SI EL ROL ES ADMIN, MOSTRAMOS EL BOTÓN EN EL NAVBAR
        if (rol === "ADMIN" && navAdminLink) {
            navAdminLink.style.setProperty('display', 'block', 'important');
        } else if (navAdminLink) {
            navAdminLink.style.display = 'none';
        }
    } else {
        if (btnLogin) btnLogin.style.display = 'block';
        if (userProfile) userProfile.style.display = 'none';

        // 🌟 SI NO HAY SESIÓN, SE OCULTA POR COMPLETO
        if (navAdminLink) navAdminLink.style.display = 'none';
    }
}

function logout() {
    localStorage.removeItem('usuarioLogueado');
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('usuarioRol'); // 🌟 LIMPIAMOS EL ROL AL CERRAR SESIÓN
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
            .then(response => response.json()) // 🌟 Cambiado de .text() a .json()
            .then(data => {
                            if (data.status === "success") {
                                localStorage.setItem('usuarioLogueado', user);
                                localStorage.setItem('jwtToken', data.token);
                                localStorage.setItem('usuarioRol', data.rol); // 🌟 GUARDAMOS EL ROL RETORNADO DE JAVA
                                alert("Bienvenido, " + user);

                                // Cerrar modal
                                const modalEl = document.getElementById('loginModal');
                                const modalInstance = bootstrap.Modal.getInstance(modalEl);
                                if (modalInstance) modalInstance.hide();

                                verificarSesion();
                                actualizarVistaCarrito();
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

    // 2. Manejo del Registro (ACTUALIZADO PARA CAMPOS PREMIUM)
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();

            // Capturamos los 4 inputs del modal
            const nombre = document.getElementById('newNombre').value;
            const email = document.getElementById('newEmail').value;
            const newUser = document.getElementById('newUser').value;
            const newPass = document.getElementById('newPassword').value;

            // Usamos URLSearchParams para empaquetar los datos tal como los espera @ModelAttribute en Java
            const datosFormulario = new URLSearchParams();
            datosFormulario.append('nombre', nombre);
            datosFormulario.append('email', email);
            datosFormulario.append('username', newUser);
            datosFormulario.append('password', newPass);

            // Enviamos la petición POST cargando los parámetros organizados
            fetch('/auth/registrar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: datosFormulario
            })
            .then(response => response.text())
            .then(data => {
                const respuestaLimpia = data.trim();

                if (respuestaLimpia === "success") {
                    alert("¡Cuenta creada con éxito para " + newUser + "! Ya puedes iniciar sesión.");
                    mostrarLogin();
                    registerForm.reset();
                } else if (respuestaLimpia.startsWith("error:")) {
                    // Muestra los mensajes profesionales exactos de tus anotaciones de Java (@Pattern, @Email)
                    const mensajeDesdeJava = respuestaLimpia.replace("error:", "").trim();
                    alert(mensajeDesdeJava);
                } else {
                    alert(respuestaLimpia);
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

async function agregarAlCarrito(productoId, stock) {
    // 🌟 1. Validación de seguridad en el front-end
    // Si el usuario intentó manipular el botón, aquí bloqueamos la petición
    if (stock === null || stock <= 0) {
        alert("Lo sentimos, este producto ya no cuenta con stock disponible.");
        return;
    }

    const username = localStorage.getItem('usuarioLogueado');
    const token = localStorage.getItem('jwtToken');

    if (!username || !token) {
        alert("Debes iniciar sesión para agregar productos al carrito.");
        const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
        loginModal.show();
        return;
    }

    try {
        const response = await fetch(`/api/carrito/agregar?username=${username}&productoId=${productoId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("Producto añadido a tu cuenta con éxito");
            actualizarVistaCarrito();
        } else if (response.status === 401 || response.status === 403) {
            alert("Tu sesión ha expirado, por favor vuelve a ingresar.");
            logout();
        } else {
            // Opcional: Manejar errores específicos del servidor (ej. si el stock cambió en el momento exacto)
            const errorData = await response.text();
            alert("Error al agregar: " + errorData);
        }
    } catch (error) {
        console.error("Error al conectar con el servidor", error);
    }
}

async function actualizarVistaCarrito() {
    const username = localStorage.getItem('usuarioLogueado');
    const token = localStorage.getItem('jwtToken'); // 🌟 Recuperamos el token

    const listaContainer = document.getElementById('listaCarrito');
    const badge = document.getElementById('carritoBadge');
    const totalLabel = document.getElementById('totalCarrito');
    const subtotalLabel = document.getElementById('subtotalCarrito');

    // Limpieza de seguridad si no hay usuario activo
    if (!username || !token) {
        if (listaContainer) listaContainer.innerHTML = '<p class="text-center text-muted py-5">Inicia sesión para ver tu carrito.</p>';
        if (badge) badge.style.display = 'none';
        if (totalLabel) totalLabel.innerText = "S/ 0.00";
        if (subtotalLabel) subtotalLabel.innerText = "S/ 0.00";
        return;
    }

    try {
        const response = await fetch(`/api/carrito/listar?username=${username}`, {
            headers: {
                'Authorization': `Bearer ${token}` // 🌟 MANDAMOS EL TOKEN
            }
        });

        if (!response.ok) return;
        const items = await response.json();

        if (items.length === 0) {
            listaContainer.innerHTML = '<p class="text-center text-muted py-5">Tu carrito está vacío.</p>';
            if (badge) badge.style.display = 'none';
            if (totalLabel) totalLabel.innerText = "S/ 0.00";
            if (subtotalLabel) subtotalLabel.innerText = "S/ 0.00";
            return;
        }

        if (badge) {
            badge.innerText = items.length;
            badge.style.display = 'block';
        }

        listaContainer.innerHTML = '';
        let total = 0;

        items.forEach(item => {
            const cantidadItem = item.cantidad || 1;
            total += (item.producto.precio * cantidadItem);

            let opcionesCantidad = `<option value="0">0 - Retirar</option>`;
            for (let i = 1; i <= 10; i++) {
                opcionesCantidad += `<option value="${i}" ${cantidadItem === i ? 'selected' : ''}>${i}</option>`;
            }

            listaContainer.innerHTML += `
                <div class="cart-item">
                    <div class="cart-item-img">
                        <img src="/img/${item.producto.imagen}" alt="${item.producto.nombre}">
                    </div>
                    <div class="cart-item-detalles">
                        <div class="d-flex justify-content-between align-items-start">
                            <span class="text-muted text-uppercase" style="font-size: 11px; font-weight: 600; letter-spacing: 0.5px;">NEXO WEAR</span>
                            <button class="btn p-0 text-danger border-0" style="font-size: 15px;" onclick="eliminarDelCarrito(${item.id})">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                        <h6 class="m-0 fw-bold text-dark mt-1" style="font-size: 13px; line-height: 1.3;">${item.producto.nombre}</h6>
                        <small class="text-muted d-block mb-1" style="font-size: 11px;">Talla: Única</small>
                        <div class="fw-bold text-dark mb-2" style="font-size: 13px;">S/ ${item.producto.precio.toFixed(2)}</div>
                        <div>
                            <select class="form-select-cantidad" onchange="cambiarCantidad(${item.id}, this.value)">
                                ${opcionesCantidad}
                            </select>
                        </div>
                    </div>
                </div>
            `;
        });

        if (totalLabel) totalLabel.innerText = `S/ ${total.toFixed(2)}`;
        if (subtotalLabel) subtotalLabel.innerText = `S/ ${total.toFixed(2)}`;
    } catch(err) {
        console.error("Error al renderizar carrito:", err);
    }
}

async function cambiarCantidad(itemId, nuevaCantidad) {
    const cantidadNumerica = parseInt(nuevaCantidad, 10);
    const token = localStorage.getItem('jwtToken'); // 🌟 Recuperamos el token

    if (cantidadNumerica === 0) {
        eliminarDelCarrito(itemId);
        return;
    }

    try {
        const response = await fetch(`/api/carrito/actualizar/${itemId}?cantidad=${cantidadNumerica}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}` // 🌟 MANDAMOS EL TOKEN
            }
        });

        if (response.ok) {
            actualizarVistaCarrito();
        } else {
            console.error(`Error del servidor: Código ${response.status}`);
        }
    } catch (error) {
        console.error("Error crítico de red:", error);
    }
}

async function eliminarDelCarrito(id) {
    if (!confirm("¿Estás seguro de quitar este producto?")) return;
    const token = localStorage.getItem('jwtToken'); // 🌟 Recuperamos el token

    try {
        const response = await fetch(`/api/carrito/eliminar/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}` // 🌟 MANDAMOS EL TOKEN
            }
        });

        if (response.ok) {
            actualizarVistaCarrito();
        } else {
            alert("No se pudo eliminar el producto.");
        }
    } catch (error) {
        console.error("Error al eliminar:", error);
    }
}

// --- ENVIAR CONSULTAS A WHATSAPP ---
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

// --- EFECTO NAVBAR SCROLL ---
window.addEventListener('scroll', function () {
    const navbar = document.querySelector('.navbar');
    if (window.scrollY > 50) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
});

// --- FILTROS DE CATÁLOGO ---
document.addEventListener("DOMContentLoaded", function() {
    const checkboxes = document.querySelectorAll('.filter-checkbox');
    const radios = document.querySelectorAll('.order-radio');

    checkboxes.forEach(cb => cb.addEventListener('change', ejecutarFiltros));
    radios.forEach(rd => rd.addEventListener('change', ejecutarFiltros));

    function ejecutarFiltros() {
        const tiposSeleccionados = Array.from(document.querySelectorAll('input[name="tipo"]:checked')).map(cb => cb.value);
        const coloresSeleccionados = Array.from(document.querySelectorAll('input[name="color"]:checked')).map(cb => cb.value);
        const tallasSeleccionadas = Array.from(document.querySelectorAll('input[name="talla"]:checked')).map(cb => cb.value);
        const metodoOrden = document.querySelector('input[name="orden"]:checked').value;

        const productos = Array.from(document.querySelectorAll('.producto-item'));
        let visiblesCount = 0;

        productos.forEach(p => {
            const pTipo = p.getAttribute('data-tipo');
            const pColor = p.getAttribute('data-color');
            const pTalla = p.getAttribute('data-talla');

            const cumpleTipo = tiposSeleccionados.length === 0 || tiposSeleccionados.includes(pTipo);
            const cumpleColor = coloresSeleccionados.length === 0 || coloresSeleccionados.includes(pColor);
            const cumpleTalla = tallasSeleccionadas.length === 0 || tallasSeleccionadas.includes(pTalla);

            if (cumpleTipo && cumpleColor && cumpleTalla) {
                p.style.setProperty('display', 'block', 'important');
                visiblesCount++;
            } else {
                p.style.setProperty('display', 'none', 'important');
            }
        });

        const mensajeVacio = document.querySelector('.id-vacio');
        if(mensajeVacio) {
            mensajeVacio.style.display = (visiblesCount === 0) ? 'block' : 'none';
        }

        const contenedor = document.getElementById('contenedorProductos');
        if (metodoOrden === 'menor-mayor') {
            productos.sort((a, b) => parseFloat(a.getAttribute('data-precio')) - parseFloat(b.getAttribute('data-precio')));
            productos.forEach(p => contenedor.appendChild(p));
        } else if (metodoOrden === 'mayor-menor') {
            productos.sort((a, b) => parseFloat(b.getAttribute('data-precio')) - parseFloat(a.getAttribute('data-precio')));
            productos.forEach(p => contenedor.appendChild(p));
        } else if (metodoOrden === 'reciente') {
            productos.sort((a, b) => parseInt(b.getAttribute('data-id')) - parseInt(a.getAttribute('data-id')));
            productos.forEach(p => contenedor.appendChild(p));
        }
    }
});

// 🌟 CONFIGURACIÓN COMPLEMENTARIA: CARGA SEGURA DEL CARRITO AL NAVEGAR 🌟
document.addEventListener('DOMContentLoaded', function() {
    const usuario = localStorage.getItem('usuarioLogueado');
    const token = localStorage.getItem('jwtToken');

    if (usuario && token) {
        // Retraso controlado de 100 milisegundos para estabilizar la sesión en la nueva pestaña
        setTimeout(() => {
            actualizarVistaCarrito();
        }, 100);
    }
});

// Asegúrate de que esta función esté fuera de cualquier otro bloque
async function abrirModalFinalizar() {
    const username = localStorage.getItem('usuarioLogueado');
    const token = localStorage.getItem('jwtToken');
    const lista = document.getElementById('listaProductosCompra');

    // 1. Mostrar el modal
    const modalElement = document.getElementById('modalFinalizarCompra');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();

    // 2. CORRECCIÓN: La ruta correcta es /api/carrito/listar
    try {
        const response = await fetch(`/api/carrito/listar?username=${username}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error("Error al obtener datos");

        const items = await response.json();
        lista.innerHTML = "";

        if (items.length === 0) {
            lista.innerHTML = "<li>El carrito está vacío.</li>";
            return;
        }

        // 3. Listar productos (ajustado a la estructura de CarritoItem)
        items.forEach(item => {
            // Asumiendo que CarritoItem tiene un objeto Producto dentro
            const li = document.createElement('li');
            li.innerHTML = `<strong>${item.producto.nombre}</strong> - Cantidad: ${item.cantidad} - S/ ${item.producto.precio}`;
            lista.appendChild(li);
        });

    } catch (error) {
        console.error("Error:", error);
        lista.innerHTML = "<li>Error al conectar con el servidor.</li>";
    }
}
function procesarCompra() {
    const direccion = document.getElementById('direccion').value;
    const tarjeta = document.getElementById('tarjeta').value;

    if (!direccion || !tarjeta) {
        alert("Por favor, completa tu dirección y número de tarjeta.");
        return;
    }

    alert("¡Compra exitosa! Gracias por tu preferencia.");

    // Limpiar carrito y cerrar modal
    localStorage.removeItem('carrito');
    const modalEl = document.getElementById('modalFinalizarCompra');
    const modal = bootstrap.Modal.getInstance(modalEl);
    if(modal) modal.hide();

    window.location.reload();
}
