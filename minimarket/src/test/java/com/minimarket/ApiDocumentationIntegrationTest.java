package com.minimarket;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiDocumentationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Test
    void publicaSwaggerYElContratoOpenApiRequerido() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("MINIMARKET PLUS API")))
                .andExpect(content().string(containsString("\"/api/productos\"")))
                .andExpect(content().string(containsString("\"/api/carrito\"")))
                .andExpect(content().string(containsString("\"/api/inventario\"")))
                .andExpect(content().string(containsString("\"/api/usuarios\"")))
                .andExpect(content().string(containsString("\"cookieAuth\"")));
    }

    @Test
    @Transactional
    void entregaEnlacesHateoasEnLosRecursosPrincipales() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Abarrotes");
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Arroz");
        producto.setPrecio(1490.0);
        producto.setStock(20);
        producto.setCategoria(categoria);
        producto = productoRepository.save(producto);

        Rol rol = rolRepository.save(new Rol("USER"));
        Usuario usuario = new Usuario();
        usuario.setUsername("cliente.demo");
        usuario.setPassword("clave-de-prueba");
        usuario.setRoles(Set.of(rol));
        usuario = usuarioRepository.save(usuario);

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(2);
        carrito = carritoRepository.save(carrito);

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidad(20);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
        inventario = inventarioRepository.save(inventario);

        mockMvc.perform(get("/api/productos/{id}", producto.getId())
                        .with(user("tester").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(containsString("/api/productos/")))
                .andExpect(jsonPath("$._links.productos.href").value(containsString("/api/productos")));

        mockMvc.perform(get("/api/carrito/{id}", carrito.getId())
                        .with(user("tester").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(containsString("/api/carrito/")))
                .andExpect(jsonPath("$._links.usuario.href").value(containsString("/api/usuarios/")))
                .andExpect(jsonPath("$._links.producto.href").value(containsString("/api/productos/")));

        mockMvc.perform(get("/api/inventario/{id}", inventario.getId())
                        .with(user("tester").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(containsString("/api/inventario/")))
                .andExpect(jsonPath("$._links.producto.href").value(containsString("/api/productos/")));

        mockMvc.perform(get("/api/usuarios/{id}", usuario.getId())
                        .with(user("tester").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$._links.self.href").value(containsString("/api/usuarios/")))
                .andExpect(jsonPath("$._links.usuarios.href").value(containsString("/api/usuarios")));
    }
}
