#!/bin/bash

# 1. VALIDACION: ¿Existe el archivo pom.xml?
if [ ! -f "pom.xml" ]; then
    echo -e "\e[31m----------------------------------------------------------\e[0m"
    echo -e "\e[31m❌ ERROR: No se encuentra el pom.xml en este directorio.\e[0m"
    echo -e "\e[33mPor favor, ubicate en la raiz del proyecto antes de correr el script.\e[0m"
    echo -e "\e[31m----------------------------------------------------------\e[0m"
    exit 1
fi

# 2. Ruta base
BASE_PATH="src/main/java/com/clientes/react"
# 3. Crear carpetas
mkdir -p $BASE_PATH/{controller,dominio/{entity,repository,facade},dto,service,mapper,exception,constantes,config}

# 4. Funcion para generar package-info con JavaDoc
create_package_info() {
    local folder=$1
    local desc=$2
    # Convertir ruta a formato punto (com.template...)
    local pkg_path=$(echo $folder | sed 's|src/main/java/||' | sed 's|/|.|g')
    
    cat <<EOF > "$folder/package-info.java"
/**
 * $desc
 */
package $pkg_path;
EOF
}

# 5. Crear archivos de utilidad con su package
echo "package com.clientes.react.exception;" > "$BASE_PATH/exception/GeneralException.java"
echo "package com.clientes.react.constantes;" > "$BASE_PATH/constantes/AppConstants.java"

# 6. Generar package-info para cada capa
echo -e "\e[34mGenerando documentacion de paquetes...\e[0m"

create_package_info "$BASE_PATH/dominio/entity" "Entidades JPA para la base de datos"
create_package_info "$BASE_PATH/dominio/repository" "Interfaces de acceso a datos de Spring Data JPA"
create_package_info "$BASE_PATH/dominio/facade" "Capa de fachada para orquestacion de servicios"
create_package_info "$BASE_PATH/dto" "Objetos de transferencia de datos para la API"
create_package_info "$BASE_PATH/mapper" "Componentes para mapeo entre entidades y DTOs"
create_package_info "$BASE_PATH/service" "Interfaces de la logica de negocio"
create_package_info "$BASE_PATH/controller" "Controladores REST que exponen los endpoints"

echo -e "\e[32m✅ ¡Exito! pom.xml validado, carpetas creadas y archivos inicializados.\e[0m"