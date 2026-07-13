package com.minimarket.controller;

import com.minimarket.assembler.InventarioModelAssembler;
import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
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
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Registro y consulta de movimientos de stock")
@SecurityRequirement(name = "cookieAuth")
public class InventarioController {

    private final InventarioService inventarioService;
    private final InventarioModelAssembler assembler;

    public InventarioController(InventarioService inventarioService, InventarioModelAssembler assembler) {
        this.inventarioService = inventarioService;
        this.assembler = assembler;
    }

    @GetMapping
    @Operation(summary = "Listar movimientos de inventario", description = "Obtiene las entradas y salidas de stock con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Movimientos recuperados correctamente")
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario() {
        List<EntityModel<Inventario>> movimientos = inventarioService.findAll().stream()
                .map(assembler::toModel)
                .toList();
        return CollectionModel.of(movimientos,
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un movimiento", description = "Obtiene un movimiento de inventario y su producto relacionado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return inventario != null
                ? ResponseEntity.ok(assembler.toModel(inventario))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Registrar un movimiento", description = "Registra una entrada o salida de stock para un producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimiento registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del movimiento no válidos")
    })
    public ResponseEntity<EntityModel<Inventario>> registrarMovimiento(@Valid @RequestBody Inventario inventario) {
        EntityModel<Inventario> model = assembler.toModel(inventarioService.save(inventario));
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un movimiento", description = "Modifica los datos del movimiento de inventario indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<EntityModel<Inventario>> actualizarMovimiento(
            @PathVariable Long id, @Valid @RequestBody Inventario inventario) {
        if (inventarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        inventario.setId(id);
        return ResponseEntity.ok(assembler.toModel(inventarioService.save(inventario)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un movimiento", description = "Elimina el movimiento de inventario indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        if (inventarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        inventarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
