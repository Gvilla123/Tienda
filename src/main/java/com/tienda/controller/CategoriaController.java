/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda.controller;

import com.tienda.Service.FirebaseStorageService;
import com.tienda.domain.Categoria;
import com.tienda.service.CategoriaService;
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
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/listado") // https:localhost/categoria/listado
    public String inicio(Model model) {
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        return "/categoria/listado"; //las vistas que yo voy a crear en el html
    }
    
    
    @PostMapping("/modificar") //https:localhost/categoria/listado
    public String modificar(Categoria categoria, Model model) {
        categoria = categoriaService.getCategoria(categoria);
        model.addAttribute("categoria", categoria);
        return "/categoria/modifica";

}
    @PostMapping("/guardar")
    public String guardar(Categoria categoria,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttrs) {

        if (!imagenFile.isEmpty()) {
            categoriaService.save(categoria);
            String rutaImagen = firebaseStorageService.cargaImagen(
                    imagenFile,
                    "categoria",
                    categoria.getIdCategoria());
            categoria.setRutaImagen(rutaImagen);
        }

        categoriaService.save(categoria);
        redirectAttrs.addFlashAttribute("todoOk",
                messageSource.getMessage("categoria.actualizado",
                        null,
                        Locale.getDefault()));

        return "redirect:/categoria/listado";
    }
    
    @PostMapping("/eliminar")
    public String eliminar(Categoria categoria,
            RedirectAttributes redirectAttrs) {
        categoria = categoriaService.getCategoria(categoria);

        if (categoria == null) {
            redirectAttrs.addFlashAttribute("error", messageSource.getMessage("categoria.error01", null, Locale.getDefault()));
        } else if (false) {
            redirectAttrs.addFlashAttribute("error", messageSource.getMessage("categoria.error02", null, Locale.getDefault()));
        } else if (categoriaService.delete(categoria)) {
            redirectAttrs.addFlashAttribute("todoOk", messageSource.getMessage("categoria.eliminado", null, Locale.getDefault()));
        } else {
            redirectAttrs.addFlashAttribute("error", messageSource.getMessage("categoria.error03", null, Locale.getDefault()));
        }
        return "redirect:/categoria/listado";

        /* va en lugar del false... categoria.getProductos() != null && !categoria.getProductos().isEmpty()*/
    }



}

