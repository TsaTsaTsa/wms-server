import argparse
import rasterio
from rasterio.windows import Window

def main():
    # Определяем аргументы командной строки
    parser = argparse.ArgumentParser()
    parser.add_argument('--input_file', type=str, required=True)
    parser.add_argument('--tile_size', type=int, default=1001)
    args = parser.parse_args()

    input_file = args.input_file
    tile_size = args.tile_size

    with rasterio.open(input_file) as dataset:
        width, height = dataset.width, dataset.height

        for i in range(0, width, tile_size):
            for j in range(0, height, tile_size):
                window = Window(i, j, tile_size, tile_size)
                data = dataset.read(window=window)

                out_meta = dataset.meta.copy()
                out_meta.update({
                    "width": int(window.width),
                    "height": int(window.height),
                    "transform": dataset.window_transform(window)
                })

                output_file = f'tile_{i}_{j}.tif'
                with rasterio.open(output_file, 'w', **out_meta) as out_raster:
                    out_raster.write(data)

if __name__ == '__main__':
    main()
