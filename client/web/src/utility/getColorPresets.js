import palette from "../theme/palette";

export const colorPresets = [

  {
    name: "default",
    ...palette.light.primary,
  },

  {
    name: "cyan",
    lighter: "#D1FFFC",
    light: "#76F2FF",
    main: "#1CCAFF",
    dark: "#0E77B7",
    darker: "#053D7A",
    contrastText: palette.light.grey[800],
  },

  {
    name: "green",
    lighter: "#D5F5E3",
    light: "#81D48E",
    main: "#4ad268",
    dark: "#3bb556",
    darker: "#32904d",
    contrastText: "#fff",
  },

  {
    name: "red",
    lighter: "#FFE3D5",
    light: "#FFC1AC",
    main: "#ea8989",
    dark: "#c25265",
    darker: "#df3d76",
    contrastText: "#fff",
  },

  {
    name: "orange",
    lighter: "#FEF4D4",
    light: "#e59e78",
    main: "#f15b2f",
    dark: "#e55019",
    darker: "#e25b23",
    contrastText: palette.light.grey[800],
  },

  {
    name: "purple",
    lighter: "#f9d6fd",
    light: "#B985F4",
    main: "#b44af1",
    dark: "#6b3dc9",
    darker: "#443089",
    contrastText: "#fff",
  },


  // {
  //   name: "blue",
  //   lighter: "#D1E9FC",
  //   light: "#76B0F1",
  //   main: "#2065D1",
  //   dark: "#103996",
  //   darker: "#061B64",
  //
  //   contrastText: "#fff",
  // },



];

export const defaultPreset = colorPresets[0];
export const cyanPreset = colorPresets[1];
export const greenPreset = colorPresets[2];
export const redPreset = colorPresets[3];
export const orangePreset = colorPresets[4];
export const purplePreset = colorPresets[5];


export default function getColorPresets(presetsKey) {
  return {
    default: defaultPreset,
    cyan: cyanPreset,
    green: greenPreset,
    red: redPreset,
    orange: orangePreset,
    purple: purplePreset,
  }[presetsKey];
}
