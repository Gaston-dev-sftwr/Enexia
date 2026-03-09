const { createApp } = Vue;

const app = createApp({
    data() {
        return {
            events: [],       // Lista de eventos disponibles
            historial: [],    // Historial de inscripciones
            evento: null,     // Objeto para la página de detalle
            loading: false
        };
    },
    mounted() {
        // Detectar en qué página estamos para disparar la carga correcta
        const path = window.location.pathname;
        if (path.includes("mod_eventos")) {
            this.fetchEvents();
        } else if (path.includes("mod_historial")) {
            this.fetchHistorial();
        } else if (path.includes("evento_detalle")) {
            this.fetchOneEvent();
        }
    },
    methods: {
        // --- CARGAR TODOS LOS EVENTOS ---
        fetchEvents() {
            this.loading = true;
            axios.get('/api/eventos/publico')
                .then(res => {
                    this.events = res.data.filter(ev => ev.estado === 1);
                })
                .catch(err => console.error("Error cargando eventos:", err))
                .finally(() => this.loading = false);
        },

        // --- CARGAR HISTORIAL (Ruta: /api/participante/historial) ---
        fetchHistorial() {
            this.loading = true;
            axios.get('/api/participante/historial', { withCredentials: true })
                .then(res => {
                    this.historial = res.data;
                })
                .catch(err => console.error("Error cargando historial:", err))
                .finally(() => this.loading = false);
        },

        // --- CARGAR UN SOLO EVENTO (Ruta: /api/eventos/{id}) ---
        fetchOneEvent() {
            const params = new URLSearchParams(window.location.search);
            const id = params.get('id');
            if (!id) return;

            this.loading = true;
            axios.get(`/api/eventos/${id}`)
                .then(res => {
                    this.evento = res.data;
                })
                .catch(err => alert("Error al cargar los detalles del evento."))
                .finally(() => this.loading = false);
        },

        // --- INSCRIBIRSE (Ruta: /api/inscribir/{id}) ---
        inscribirse(id, nombre) {
            axios.post(`/api/inscribir/${id}`, {}, { withCredentials: true })
                .then(res => {
                    alert(`¡Inscripción exitosa a: ${nombre}!`);
                    window.location.href = "mod_historial.html";
                })
                .catch(err => {
                    if (err.response && err.response.status === 409) {
                        alert("Ya estás inscrito en este evento.");
                    } else {
                        alert("No se pudo procesar la inscripción. Revisa tu sesión.");
                    }
                });
        },

        // --- CANCELAR (Ruta: /api/cancelar-inscripcion/{id}) ---
        cancelarInscripcion(insc) {
            if (confirm(`¿Estás seguro de cancelar tu lugar en ${insc.eventoNombre}?`)) {
                axios.put(`/api/cancelar-inscripcion/${insc.id}`, {}, { withCredentials: true })
                    .then(() => {
                        alert("Inscripción cancelada correctamente.");
                        this.fetchHistorial();
                    })
                    .catch(err => alert("Error al cancelar la inscripción."));
            }
        },

        // --- UTILIDADES ---
        formatDateTime(fecha, hora) {
            if (!fecha) return 'A confirmar';
            const dateObj = new Date(fecha + 'T00:00:00');
            const dateStr = dateObj.toLocaleDateString('es-ES', { day: 'numeric', month: 'long', year: 'numeric' });
            return `${dateStr} a las ${hora}:00 hs`;
        },

        cerrarSesion() {
                    // 1. Limpiamos el almacenamiento local por seguridad de la UI
                    localStorage.clear();
                    sessionStorage.clear();

                    // 2. Llamamos al endpoint de logout del backend
                    // Usamos 'withCredentials: true' para que mande la cookie de sesión (JSESSIONID)
                    axios.post('/api/logout', {}, { withCredentials: true })
                        .then(() => {
                            // 3. Recién cuando el servidor confirma el cierre, redirigimos
                            window.location.href = "login.html";
                        })
                        .catch(err => {
                            console.error("Error al invalidar sesión en servidor:", err);
                            // Si falla el servidor, forzamos la salida visual de todos modos
                            window.location.href = "login.html";
                        });
                },
    }
});

app.mount('#app');