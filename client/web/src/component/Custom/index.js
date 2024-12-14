import PropTypes from 'prop-types';
import CustomDrawer from './drawer';
import ThemeContrast from './ThemeContrast';
import ThemeColorPresets from './ThemeColorPresets';



ThemeSettings.propTypes = {
  children: PropTypes.node.isRequired,
};

export default function ThemeSettings({ children }) {
  return (
    <ThemeColorPresets>
      <ThemeContrast>
          {children}
          <CustomDrawer />
      </ThemeContrast>
    </ThemeColorPresets>
  );
}
