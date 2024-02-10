import {Create, ReferenceInput, SimpleForm, TextInput} from 'react-admin';

export const PostCreateView = () => (
	<Create>
		<SimpleForm>
			<ReferenceInput source="userId" reference="users" />
			<TextInput source="title" />
			<TextInput source="body" />
		</SimpleForm>
	</Create>
);