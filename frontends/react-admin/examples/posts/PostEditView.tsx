import {Edit, ReferenceInput, SimpleForm, TextInput} from 'react-admin';

export const PostEditView = () => (
	<Edit>
		<SimpleForm>
			<TextInput source="id" disabled />
			<TextInput source="userId" reference="users" disabled />
			<TextInput source="title" />
			<TextInput source="body" />
		</SimpleForm>
	</Edit>
);