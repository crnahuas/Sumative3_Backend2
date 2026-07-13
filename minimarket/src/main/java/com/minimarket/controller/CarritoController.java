package com.minimarket.controller;

import com.minimarket.assembler.CarritoModelAssembler;
import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Operaciones de productos agregados al carrito de compra")
@SecurityRequirement(name = "cookieAuth")
public class CarritoController {

    private final CarritoService carritoService;
    private final CarritoModelAssembler assembler;

    public CarritoController(CarritoService carritoService, CarritoModelAssembler assembler) {
        this.carritoService = carritoService;
        this.assembler = assembler;
    }

    @GetMapping
    @Operation(summary = "Listar el carrito", description = "Obtiene todos los elementos del carrito con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Elementos del carrito recuperados correctamente")
    public CollectionModel<EntityModel<Carrito>> listarCarrito() {
        List<EntityModel<Carrito>> elementos = carritoService.findAll().stream()
                .map(assembler::toModel)
                .toList();
        return CollectionModel.of(elementos,
                linkTo(methodOn(CarritoController.class).listarCarrito()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un elemento del carrito", description = "Obtiene un elemento y sus enlaces al usuario y producto relacionados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Elemento del carrito encontrado"),
            @ApiResponse(responseCode = "404", description = "Elemento del carrito no encontrado")
    })
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        return carrito != null
                ? ResponseEntity.ok(assembler.toModel(carrito))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Agregar un producto al carrito", description = "Registra un producto y su cantidad en el carrito de un usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto agregado al carrito"),
            @ApiResponse(responseCode = "400", description = "Datos del carrito no válidos")
    })
    public ResponseEntity<EntityModel<Carrito>> agregarProductoAlCarrito(@Valid @RequestBody Carrito carrito) {
        EntityModel<Carrito> model = assembler.toModel(carritoService.save(carrito));
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un elemento del carrito", description = "Modifica el producto o cantidad del elemento indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Elemento del carrito actualizado"),
            @ApiResponse(responseCode = "404", description = "Elemento del carrito no encontrado")
    })
    public ResponseEntity<EntityModel<Carrito>> actualizarCarrito(
            @PathVariable Long id, @Valid @RequestBody Carrito carrito) {
        if (carritoService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        carrito.setId(id);
        return ResponseEntity.ok(assembler.toModel(carritoService.save(carrito)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto del carrito", description = "Quita el elemento indicado del carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Elemento eliminado del carrito"),
            @ApiResponse(responseCode = "404", description = "Elemento del carrito no encontrado")
    })
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        if (carritoService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        carritoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
