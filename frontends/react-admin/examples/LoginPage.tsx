import {useState} from 'react'
import {useLogin, useNotify, Notification} from 'react-admin';
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import {useForm, Controller} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import * as Yup from 'yup';
 
export const LoginPage = () => {
	const [username, setUsername] = useState('');
	const [password, setPassword] = useState('');
	const login = useLogin();
	const notify = useNotify();
	
	const validationSchema = Yup.object().shape({
		username: Yup.string().required('Username is required.').
			min(6, 'Username must be at least 6 characters.').
			max(32, 'Username must not exceed 32 characters.'),
		password: Yup.string().required('Password is required.').
			min(6, 'Password must be at least 6 characters.').
			max(16, 'Password must not exceed 16 characters.')
	});
	
	const onSubmit = (data) => {
		login(data).catch(error => notify(`Error: ${error.message}`));
	};
 
	const {
		register,
		control,
		handleSubmit,
		formState: {errors}
	} = useForm({
		resolver: yupResolver(validationSchema)
	});
	
	return (
		<Container component="main" maxWidth="xs">
			<Box
				sx={{
					marginTop: 8,
					display: "flex",
					flexDirection: "column",
					alignItems: "center",
				}}>
				<Typography variant="h5">
					Login in
				</Typography>
				<Box component="form" onSubmit={handleSubmit(onSubmit)}
						noValidate sx={{mt: 1}}>
					<TextField margin="normal" required fullWidth
						id="username" label="Username"
						name="username" autoFocus
						{...register('username')}
						error={errors.username ? true : false} />
					<Typography variant="inherit" color="textSecondary">
						{errors.username?.message}
					</Typography>
					<TextField margin="normal" required fullWidth
						id="password" name="password" label="Password"
						type="password" {...register('password')}
						error={errors.password ? true : false} />
					<Typography variant="inherit" color="textSecondary">
						{errors.password?.message}
					</Typography>
					<Button type="submit" fullWidth variant="contained"
						sx={{mt: 3, mb: 2}}>Login in</Button>
				</Box>
			</Box>
		</Container>
	);
};
