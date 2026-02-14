import React from 'react';

export const FooterComponent = () => {
    return (
        <footer>
            <nav className="navbar fixed-bottom navbar-light bg-light justify-content-center">
                <span className='text-muted'>
                    Sistema desarrollado por La Tecnol... {new Date().getFullYear()}
                </span>
            </nav>
        </footer>
    );
}

export default FooterComponent;