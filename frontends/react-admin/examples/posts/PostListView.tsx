import {List, Datagrid, TextField, ReferenceField, EditButton} from "react-admin";

export const PostListView = () => (
	<List hasCreate={true}>
		<Datagrid rowClick="show">
			<ReferenceField source="userId" reference="users" />
			<TextField source="id" />
			<TextField source="title" />
			<EditButton />
		</Datagrid>
	</List>
);