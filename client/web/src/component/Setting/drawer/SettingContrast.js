
import { styled } from '@mui/material/styles';
import { Grid2, RadioGroup, CardActionArea } from '@mui/material';
import useSettings from '../../../hook/useSettings';
import Iconify from '../../Iconify';
import BoxMask from './BoxMask';


const BoxStyle = styled(CardActionArea)(({ theme }) => ({
  height: 72,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: theme.palette.text.disabled,
  border: `solid 1px ${theme.palette.grey[500_12]}`,
  borderRadius: Number(theme.shape.borderRadius) * 1.25,
}));


export default function SettingContrast() {
  const { themeContrast, onChangeContrast } = useSettings();

  return (
    <RadioGroup name="themeContrast" value={themeContrast} onChange={onChangeContrast}>
      <Grid2 dir="ltr" container spacing={2.5}>
        {['default', 'bold'].map((contrast, index) => {
          const isSelected = themeContrast === contrast;

          return (
            <Grid2 key={contrast} item xs={6}>
              <BoxStyle
                sx={{
                  ...(isSelected && {
                    color: 'primary.main',
                    boxShadow: (theme) => theme.customShadows.z20,
                  }),
                }}
              >
                <Iconify icon={index === 0 ? 'cil:contrast' : 'ion:contrast-outline'} width={28} height={28} />
                <BoxMask value={contrast} />
              </BoxStyle>
            </Grid2>
          );
        })}
      </Grid2>
    </RadioGroup>
  );
}
