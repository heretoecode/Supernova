import asyncio
import os
from playwright.async_api import async_playwright

async def generate_screenshots():
    mockup_path = os.path.abspath("docs/mockups/index.html")
    output_dir = os.path.abspath("docs/mockups/screenshots")
    os.makedirs(output_dir, exist_ok=True)

    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        page = await browser.new_page(viewport={"width": 1920, "height": 1080})

        await page.goto(f"file://{mockup_path}")
        await page.wait_for_timeout(1000)

        # Screens to capture
        screens = [
            ("01_home_featured.png", "home-screen"),
            ("02_movies_grid.png", "movies-grid-screen"),
            ("03_movies_list.png", "movies-list-screen"),
            ("04_movie_details.png", "details-screen"),
            ("05_network_sources.png", "network-screen"),
            ("06_settings.png", "settings-screen"),
            ("07_player_hud.png", "player-hud-screen"),
        ]

        for filename, screen_id in screens:
            await page.evaluate(f"showScreen('{screen_id}')")
            await page.wait_for_timeout(500)
            target_path = os.path.join(output_dir, filename)
            await page.screenshot(path=target_path, full_page=False)
            print(f"Captured: {filename}")

        await browser.close()

if __name__ == "__main__":
    asyncio.run(generate_screenshots())
