package com.example.productcatalog.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

@Route("products") // This maps the view to http://localhost:8080/products
@PageTitle("Product Catalog")
public class ProductListView extends VerticalLayout {

    public ProductListView() {
        // Create a Grid component to display data in a table
        Grid<String> grid = new Grid<>();
        
        // Define the column for the product name
        grid.addColumn(productName -> productName).setHeader("Product Name");
        
        // Add some sample data
        grid.setItems("Laptop", "Smartphone", "Wireless Headphones", "Mechanical Keyboard");
        
        // Add the grid to the view
        add(grid);
    }
}