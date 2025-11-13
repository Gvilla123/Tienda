/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda.controller;

import com.tienda.Service.FirebaseStorageService;
import com.tienda.domain.Producto;
import com.tienda.service.ProductoService;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author 
 */
@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/listado") // https:localhost/producto/listado
    public String inicio(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        return "/producto/listado"; //las vistas que yo voy a crear en el html
    }
    
    
    @PostMapping("/modificar") //https:localhost/producto/listado
    public String modificar(Producto producto, Model model) {
        producto = productoService.getProducto(producto);
        model.addAttribute("producto", producto);
        return "/producto/modifica";

}

    /**
     *
     * @param producto
     * @param imagenFile
     * @param redirectAttrs
     * @return
     */
    @PostMapping("/guardar")
    public String guardar(Producto producto,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttrs) {

        if (!imagenFile.isEmpty()) {
            productoService.save(producto);
            String rutaImagen;
            rutaImagen = firebaseStorageService.cargaImagen(
                     imagenFile,"producto",producto.getIdProducto());
            producto.setRutaImagen(rutaImagen);
        }

        productoService.save(producto);
        redirectAttrs.addFlashAttribute("todoOk",
                messageSource.getMessage("producto.actualizado",
                        null,
                        Locale.getDefault()));

        return "redirect:/producto/listado";
    }
    
    @PostMapping("/eliminar")
    public String eliminar(Producto producto,
            RedirectAttributes redirectAttrs) {
        producto = productoService.getProducto(producto);

        if (producto == null) {
            redirectAttrs.addFlashAttribute("error", messageSource.getMessage("producto.error01", null, Locale.getDefault()));
        } else if (false) {
            
            redirectAttrs.addFlashAttribute("error", messageSource.getMessage("producto.error02", null, Locale.getDefault()));
        } else if (!productoService.delete(producto)) {
            redirectAttrs.addFlashAttribute("error", messageSource.getMessage("producto.error03", null, Locale.getDefault()));
        } else {
            redirectAttrs.addFlashAttribute("todoOk", messageSource.getMessage("producto.eliminado", null, Locale.getDefault()));
        }
        
        return "redirect:/producto/listado";

        /* va en lugar del false... producto.getProductos() != null && !producto.getProductos().isEmpty()*/
    }



}

