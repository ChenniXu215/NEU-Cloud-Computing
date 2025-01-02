resource "aws_route53_record" "dev_a_record" {
  zone_id = var.dev_zone_id
  name    = "${var.dev_subdomain_name}.${var.zone_name}"
  type    = "A"

  alias {
    name                   = aws_lb.web_app_lb.dns_name
    zone_id                = aws_lb.web_app_lb.zone_id
    evaluate_target_health = true
  }
}

resource "aws_route53_record" "dev_aaaa_record" {
  zone_id = var.dev_zone_id
  name    = "${var.dev_subdomain_name}.${var.zone_name}"
  type    = "AAAA"

  alias {
    name                   = aws_lb.web_app_lb.dns_name
    zone_id                = aws_lb.web_app_lb.zone_id
    evaluate_target_health = true
  }
}
