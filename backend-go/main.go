package main

import "github.com/gofiber/fiber/v2"

func main() {
	app := fiber.New()
	app.Post("/get_into_db", func(c *fiber.Ctx) error {
		return c.SendString("Hello")
	})
}
