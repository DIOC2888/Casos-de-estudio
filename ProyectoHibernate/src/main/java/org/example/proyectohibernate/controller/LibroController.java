package org.example.proyectohibernate.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohibernate.dao.LibroDAO;
import org.example.proyectohibernate.model.Libro;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class LibroController {
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private TextField txtAnio;
    @FXML
    private TextField txtPrecio;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnGuardar;

    @FXML
    private TableView<Libro> tblTabla;
    @FXML
    private TableColumn<Libro, Integer> colId;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, String> colAnio;
    @FXML
    private TableColumn<Libro, Double> colPrecio;

    private final LibroDAO libroDAO = new LibroDAO();

    private Long idSeleccionado = null;


    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        tblTabla.getSelectionModel().selectedItemProperty().addListener((
                obs, oldValue, newValue) -> {
            if (newValue != null) {
                selectLibro(newValue);
            }else{
                idSeleccionado = null;
                actualizarBotones();
            }
        });
        actualizarBotones();
        clickCargar();
    }


    private void selectLibro(Libro libro) {
        idSeleccionado = libro.getId();
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtAnio.setText(String.valueOf(libro.getAnio()));
        txtPrecio.setText(String.valueOf(libro.getPrecio()));
        actualizarBotones();
    }

    private void actualizarBotones() {
        boolean sinSeleccio = idSeleccionado == null;
        btnModificar.setDisable(sinSeleccio);
        btnGuardar.setDisable(!sinSeleccio);
    }

    private Libro obtenerLibro() {
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        int anio = Integer.valueOf(txtAnio.getText());
        BigDecimal precio = BigDecimal.valueOf(Double.valueOf(txtPrecio.getText()));
        return new Libro(0, titulo, autor, anio, precio);
    }
    @FXML
    private void clickGuardar() {
       try{
              Libro libro = obtenerLibro();
              libroDAO.guardar(libro);
              clickLimpiar();
              clickCargar();
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
    @FXML
    private void clickModificar(){
        try{
            Libro libro = obtenerLibro();
            libro.setId(idSeleccionado);
            boolean modificado = libroDAO.modificar(libro);

            if(!modificado) {

            }
            clickLimpiar();
            clickCargar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clickEliminar(){
        try{
            Alert confirmacion  = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmacion de eliminacion de datos.");
            confirmacion.setHeaderText("Eliminar libro");
            confirmacion.setContentText("¿Desea eliminar el libro seleccionado?");

            Optional<ButtonType> result = confirmacion.showAndWait();
            if(result.isEmpty() || result.get() != ButtonType.OK){
                return;
            }

            boolean eliminado = libroDAO.eliminar(idSeleccionado);
            if(!eliminado) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Error al eliminar libro");
                alerta.setHeaderText("No se pudo eliminar el libro");
                alerta.setContentText("El libro no se pudo eliminar. Por favor, intente nuevamente.");
                alerta.showAndWait();
                return;
            }
            clickLimpiar();
            clickCargar();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML private void clickCargar(){
        try{
            List<Libro > libros = libroDAO.listar();
            clickLimpiar();
            tblTabla.setItems(FXCollections.observableArrayList(libros));

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    private void clickLimpiar(){
        tblTabla.getSelectionModel().clearSelection();

        idSeleccionado = null;
        txtTitulo.clear();
        txtAutor.clear();
        txtAnio.clear();
        txtPrecio.clear();

        actualizarBotones();

    }
}
