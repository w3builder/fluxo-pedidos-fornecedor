-- Dados de teste para o sistema de pedidos

-- Verificar se as tabelas existem antes de tentar limpar
DELETE FROM itens_pedido WHERE EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ITENS_PEDIDO');
DELETE FROM pedidos WHERE EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PEDIDOS');
DELETE FROM produtos WHERE EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PRODUTOS');
DELETE FROM clientes WHERE EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CLIENTES');
DELETE FROM telefones WHERE EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'TELEFONES');
DELETE FROM contatos WHERE EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CONTATOS');
DELETE FROM enderecos WHERE EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ENDERECOS');
DELETE FROM revendas WHERE EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'REVENDAS');

-- Inserção de Revendas
INSERT INTO revendas (id, cnpj, razao_social, nome_fantasia, email) VALUES 
(1, '12.345.678/0001-99', 'Revenda Principal LTDA', 'Revenda Principal', 'contato@revendaprincipal.com.br'),
(2, '98.765.432/0001-88', 'Revenda Secundária LTDA', 'Revenda Secundária', 'contato@revendasecundaria.com.br');

-- Inserção de Telefones para Revendas
INSERT INTO telefones (id, numero, revenda_id) VALUES 
(1, '(11) 3333-4444', 1),
(2, '(11) 98888-7777', 1),
(3, '(21) 3333-2222', 2),
(4, '(21) 97777-6666', 2);

-- Inserção de Contatos para Revendas
INSERT INTO contatos (id, nome, principal, revenda_id) VALUES 
(1, 'João Silva', true, 1),
(2, 'Maria Souza', false, 1),
(3, 'Carlos Oliveira', true, 2),
(4, 'Ana Santos', false, 2);

-- Inserção de Endereços para Revendas
INSERT INTO enderecos (id, logradouro, numero, complemento, bairro, cidade, estado, cep, revenda_id) VALUES 
(1, 'Avenida Paulista', '1000', 'Sala 101', 'Bela Vista', 'São Paulo', 'SP', '01310-100', 1),
(2, 'Rua Vergueiro', '500', 'Andar 5', 'Liberdade', 'São Paulo', 'SP', '01504-001', 1),
(3, 'Avenida Rio Branco', '100', 'Sala 201', 'Centro', 'Rio de Janeiro', 'RJ', '20040-001', 2),
(4, 'Avenida Atlântica', '2000', 'Cobertura', 'Copacabana', 'Rio de Janeiro', 'RJ', '22021-001', 2);

-- Inserção de Clientes
INSERT INTO clientes (id, nome, cpf_cnpj, email, telefone, revenda_id) VALUES 
(1, 'Cliente Empresa A', '11.222.333/0001-44', 'contato@empresaa.com.br', '(11) 5555-1111', 1),
(2, 'Cliente Pessoa B', '222.333.444-55', 'clienteb@email.com', '(11) 99222-3333', 1),
(3, 'Cliente Empresa C', '33.444.555/0001-66', 'contato@empresac.com.br', '(21) 5555-2222', 2),
(4, 'Cliente Pessoa D', '444.555.666-77', 'cliented@email.com', '(21) 99444-5555', 2);

-- Inserção de Produtos
INSERT INTO produtos (id, codigo, nome, descricao, preco, revenda_id) VALUES 
(1, 'PROD-001', 'Produto Premium', 'Produto de alta qualidade', 199.90, 1),
(2, 'PROD-002', 'Produto Standard', 'Produto padrão para uso diário', 99.90, 1),
(3, 'PROD-003', 'Produto Basic', 'Produto básico e econômico', 49.90, 1),
(4, 'PROD-101', 'Produto Gold', 'Produto premium linha ouro', 299.90, 2),
(5, 'PROD-102', 'Produto Silver', 'Produto intermediário linha prata', 149.90, 2),
(6, 'PROD-103', 'Produto Bronze', 'Produto básico linha bronze', 79.90, 2);

-- Inserção de Pedidos (adicionar campo revenda_id)
INSERT INTO pedidos (id, numero, cliente_id, revenda_id, data_hora, status, valor_total) VALUES 
(1, 'PED20230101001', 1, 1, '2023-01-01 10:30:00', 'CONCLUIDO', 399.80),
(2, 'PED20230102001', 2, 1, '2023-01-02 14:45:00', 'PENDENTE', 149.85),
(3, 'PED20230103001', 1, 1, '2023-01-03 09:15:00', 'EM_PROCESSAMENTO', 299.70),
(4, 'PED20230201001', 3, 2, '2023-02-01 11:00:00', 'CONCLUIDO', 599.80),
(5, 'PED20230202001', 4, 2, '2023-02-02 16:30:00', 'CANCELADO', 229.80),
(6, 'PED20230203001', 3, 2, '2023-02-03 10:45:00', 'PENDENTE', 749.70);

-- Inserção de Itens de Pedido
INSERT INTO itens_pedido (id, pedido_id, produto_id, quantidade, preco_unitario, valor_total) VALUES 
(1, 1, 1, 2, 199.90, 399.80),
(2, 2, 2, 1, 99.90, 99.90),
(3, 2, 3, 1, 49.90, 49.90),
(4, 3, 1, 1, 199.90, 199.90),
(5, 3, 2, 1, 99.90, 99.90),
(6, 4, 4, 2, 299.90, 599.80),
(7, 5, 5, 1, 149.90, 149.90),
(8, 5, 6, 1, 79.90, 79.90),
(9, 6, 4, 1, 299.90, 299.90),
(10, 6, 5, 3, 149.90, 449.70); 