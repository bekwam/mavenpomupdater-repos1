package com.bekwam.mavenpomupdater;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Conditionally displays cell under a warning style if parseError is set
 * 
 * @author carlwalker
 * @since 1.0.0
 */
public class WarningCellFactory implements Callback<TableColumn<POMObject,String>, TableCell<POMObject,String>>{

	@Override
	public TableCell<POMObject, String> call(TableColumn<POMObject, String> arg0) {
		return new TableCell<POMObject, String>() {
			@Override
			protected void updateItem(String arg0, boolean empty) {
				super.updateItem(arg0, empty);
				if( !empty ) {
						this.setText( arg0 );
						POMObject pomObject = (POMObject) this.getTableRow().getItem();
						if( pomObject != null && pomObject.getParseError() ) {
							this.setTextFill(Color.RED);
						} else {
							this.setTextFill(Color.BLACK);
						}
				} else {
					this.setText( null );  // clear from recycled obj
					this.setTextFill(Color.BLACK);
				}
			}
		};
	}
}
