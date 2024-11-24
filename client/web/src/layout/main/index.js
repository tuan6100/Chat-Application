import { Container, Stack } from "@mui/material";
import React from "react";
import { Outlet } from "react-router-dom";

const MainLayout = () => {
    return (
        <Container sx={{ mt: 5 }} maxWidth="sm">
            <Stack spacing={5}>
                <Stack sx={{ width: "100%" }} direction="column" alignItems="center">
                    <img style={{ height: 120, width: 120 }} src="https://www.pixiv.net/artworks/113579214" alt="Logo" />
                </Stack>
            </Stack>
            <Outlet />
        </Container>
    );
};

export default MainLayout;
