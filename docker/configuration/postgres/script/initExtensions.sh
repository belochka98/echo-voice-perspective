create_user_with_db() {
    echo "  Try to create database: '$1' with owner: '$2'"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER $2 WITH ENCRYPTED PASSWORD '$2';
    CREATE DATABASE $1 OWNER $2;
EOSQL
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" "$1" <<-EOSQL
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
EOSQL
    echo "  Finished creating with exit code $?"
}

create_user_with_db 'user_db' 'evoice_user_db'
create_user_with_db 'product_db' 'evoice_product_db'
create_user_with_db 'keycloak_db' 'evoice_keycloak_db'