package RestInn.service;

import RestInn.entities.Imagen;
import RestInn.repositories.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagenService {
    @Autowired
    private ImagenRepository imagenRepository;

    public Imagen guardarImagen(MultipartFile archivo) throws Exception {
        Imagen imagen = new Imagen();
        imagen.setNombre(archivo.getOriginalFilename());
        imagen.setTipo(archivo.getContentType());
        imagen.setDatos(archivo.getBytes());
        return imagenRepository.save(imagen);
    }

    public Imagen obtenerImagen(Long id) {
        return imagenRepository.findById(id).orElse(null);
    }
}