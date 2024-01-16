import {ReferenceField, Show, SimpleShowLayout, TextField} from 'react-admin';

export const PostShowView = () => (
	<Show>
		<SimpleShowLayout>
			<TextField source="id" />
			<ReferenceField source="userId" reference="users" />
			<TextField source="title" />
			<TextField source="body" />
		</SimpleShowLayout>
	</Show>
);