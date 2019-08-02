package com.cronoteSys.controller.components.cellfactory;

import com.cronoteSys.controller.components.listcell.TeamMemberCellController;
import com.cronoteSys.model.interfaces.ThreatingUser;
import com.cronoteSys.model.vo.relation.side.TeamMember;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TeamMemberCellFactory implements Callback<ListView<ThreatingUser>, ListCell<ThreatingUser>> {
	@Override
	public ListCell<ThreatingUser> call(ListView<ThreatingUser> listview) {
		ListCell<ThreatingUser> cell = new TeamMemberCellController(listview.getWidth());
		return cell;
	}
}