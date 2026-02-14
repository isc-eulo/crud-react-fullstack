import './App.css';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';

// --- MÓDULO: CLIENTES (Carpeta: 'components' en plural) ---
import ListClientesComponent from './components/ListClientesComponent';
import AddClienteComponent from './components/AddClienteComponent';

// --- MÓDULO: PRODUCTOS (Carpeta: 'mvccrud/component' en singular según tu imagen) ---
import ListProductoComponent from './mvccrud/component/ListProductoComponent.js';
import AddProductoComponent from './mvccrud/component/AddProductoComponent';

// --- ESTRUCTURA ---
import HeaderComponent from './components/HeaderComponent';
import FooterComponent from './components/FooterComponent';

/**
 * APP PRINCIPAL
 * Este es el Shell que orquestra los dos módulos: Clientes (Sincrónico) y Productos (Reactivo).
 */
function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <BrowserRouter>
        <HeaderComponent />
        
        {/* NAVEGACIÓN PRINCIPAL */}
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-4 shadow">
          <div className="container">
            <Link className="navbar-brand fw-bold" to="/">SISTEMA CENTRAL</Link>
            
            <div className="collapse navbar-collapse">
              <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                <li className="nav-item">
                  <Link className="nav-link btn btn-outline-secondary text-white mx-1" to="/clientes">
                    👥 Gestión Clientes
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link btn btn-outline-info text-white mx-1" to="/productos">
                    ⚡ Inventario Reactivo
                  </Link>
                </li>
              </ul>
            </div>
          </div>
        </nav>

        {/* CONTENEDOR DE VISTAS */}
        <main className="container flex-grow-1">
          <Routes>
            {/* Inicio / Dashboard */}
            <Route path='/' element={
              <div className="text-center mt-5 p-5 bg-light rounded shadow-sm">
                <h1 className="display-4">Panel de Control MVC</h1>
                <p className="lead">Bienvenido al sistema integrado de gestión.</p>
                <hr className="my-4" />
                <div className="d-flex justify-content-center gap-3">
                  <Link to="/clientes" className="btn btn-primary btn-lg">Ir a Clientes</Link>
                  <Link to="/productos" className="btn btn-info btn-lg">Ir a Productos</Link>
                </div>
              </div>
            } />

            {/* RUTAS: CLIENTES */}
            <Route path='/clientes' element={<ListClientesComponent />} />
            <Route path='/add-cliente' element={<AddClienteComponent />} />
            <Route path='/edit-cliente/:id' element={<AddClienteComponent />} />

            {/* RUTAS: PRODUCTOS (MODO REACTIVO) */}
            <Route path='/productos' element={<ListProductoComponent />} />
            <Route path='/add-producto' element={<AddProductoComponent />} />
            <Route path='/edit-producto/:id' element={<AddProductoComponent />} />
          </Routes>
        </main>

        <FooterComponent />
      </BrowserRouter>
    </div>
  );
}

export default App;