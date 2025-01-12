import { Box } from '@mui/material';
import React, { forwardRef } from 'react';

const ScrollBar = forwardRef(({ children, sx, onScroll, ...other }, ref) => {
  return (
      <Box
          ref={ref}
          onScroll={onScroll}
          sx={{
            overflowY: 'auto',
            overflowX: 'hidden',
            ...sx,
          }}
          {...other}
      >
        {children}
      </Box>
  );
});

export default ScrollBar;
