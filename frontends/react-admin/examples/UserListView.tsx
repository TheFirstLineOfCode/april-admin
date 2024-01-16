import {Datagrid, EmailField, List, TextField, TextInput} from 'react-admin';

const userFilters = [
	<TextInput label="Name" source="name" alwaysOn />,
	<TextInput label="Company" source="company.name" />,
];

export const UserListView = () => (
	<List filters = {userFilters}>
		<Datagrid bulkActionButtons={false} rowClick="show">
			<TextField source="id" />
			<TextField source="name" />
			<EmailField source="email" />
			<TextField source="website" />
			<TextField source="company.name" />
        </Datagrid>
    </List>
);